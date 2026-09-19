package com.practice.samplespringboot.service;

import com.practice.samplespringboot.entity.Account;
import com.practice.samplespringboot.entity.Alert;
import com.practice.samplespringboot.entity.RuleConfig;
import com.practice.samplespringboot.entity.Transaction;
import com.practice.samplespringboot.kafka.AlertProducer;
import com.practice.samplespringboot.repository.AccountRepository;
import com.practice.samplespringboot.repository.AlertRepository;
import com.practice.samplespringboot.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DetectionEngineService {

    private static final Logger log = LoggerFactory.getLogger(DetectionEngineService.class);

    private final RuleConfigCacheService ruleConfigCacheService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AlertRepository alertRepository;
    private final AlertProducer alertProducer;

    public DetectionEngineService(RuleConfigCacheService ruleConfigCacheService,
                                  TransactionRepository transactionRepository,
                                  AccountRepository accountRepository,
                                  AlertRepository alertRepository,
                                  AlertProducer alertProducer) {
        this.ruleConfigCacheService = ruleConfigCacheService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.alertRepository = alertRepository;
        this.alertProducer = alertProducer;
    }

    public void processTransaction(Transaction txn) {
        log.info("Evaluating transaction [{}] for account [{}] against AML detection rules...", txn.getTransactionId(), txn.getAccountId());
        
        // Save transaction to DB first
        transactionRepository.save(txn);

        List<String> triggeredRules = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        int accumulatedRiskScore = 0;

        BigDecimal txnAmount = txn.getNormalizedAmount() != null ? txn.getNormalizedAmount() : txn.getAmount();
        if (txnAmount == null) txnAmount = BigDecimal.ZERO;

        // Rule 1: Large Transaction (CTR Threshold)
        if (ruleConfigCacheService.isRuleEnabled("LARGE_TRANSACTION")) {
            RuleConfig cfg = ruleConfigCacheService.getRuleConfig("LARGE_TRANSACTION");
            BigDecimal threshold = cfg != null && cfg.getThresholdAmount() != null ? cfg.getThresholdAmount() : new BigDecimal("10000.00");
            if (txnAmount.compareTo(threshold) >= 0) {
                triggeredRules.add("LARGE_TRANSACTION");
                evidence.add("Transaction amount $" + txnAmount + " exceeds CTR threshold $" + threshold);
                accumulatedRiskScore += cfg != null && cfg.getRiskWeight() != null ? cfg.getRiskWeight() : 40;
            }
        }

        // Rule 2: Structuring / Smurfing
        if (ruleConfigCacheService.isRuleEnabled("STRUCTURING")) {
            RuleConfig cfg = ruleConfigCacheService.getRuleConfig("STRUCTURING");
            int windowHours = cfg != null && cfg.getTimeWindowHours() != null && cfg.getTimeWindowHours() > 0 ? cfg.getTimeWindowHours() : 24;
            LocalDateTime windowStart = txn.getTimestamp() != null ? txn.getTimestamp().minusHours(windowHours) : LocalDateTime.now().minusHours(windowHours);
            List<Transaction> recentTxns = transactionRepository.findByAccountIdAndTimestampBetween(txn.getAccountId(), windowStart, txn.getTimestamp() != null ? txn.getTimestamp() : LocalDateTime.now());
            
            long structuringCount = recentTxns.stream()
                    .filter(t -> {
                        BigDecimal amt = t.getNormalizedAmount() != null ? t.getNormalizedAmount() : t.getAmount();
                        return amt != null && amt.compareTo(new BigDecimal("9000.00")) >= 0 && amt.compareTo(new BigDecimal("9999.99")) <= 0;
                    })
                    .count();

            if (structuringCount >= 3) {
                triggeredRules.add("STRUCTURING");
                evidence.add("Detected " + structuringCount + " transactions between $9,000-$9,999 within " + windowHours + "h window");
                accumulatedRiskScore += cfg != null && cfg.getRiskWeight() != null ? cfg.getRiskWeight() : 50;
            }
        }

        // Rule 3: Rapid Movement of Funds
        if (ruleConfigCacheService.isRuleEnabled("RAPID_FUND_MOVEMENT")) {
            RuleConfig cfg = ruleConfigCacheService.getRuleConfig("RAPID_FUND_MOVEMENT");
            int windowHours = cfg != null && cfg.getTimeWindowHours() != null && cfg.getTimeWindowHours() > 0 ? cfg.getTimeWindowHours() : 48;
            LocalDateTime windowStart = txn.getTimestamp() != null ? txn.getTimestamp().minusHours(windowHours) : LocalDateTime.now().minusHours(windowHours);
            List<Transaction> recentTxns = transactionRepository.findByAccountIdAndTimestampBetween(txn.getAccountId(), windowStart, txn.getTimestamp() != null ? txn.getTimestamp() : LocalDateTime.now());

            BigDecimal totalDeposits = recentTxns.stream()
                    .filter(t -> "DEPOSIT".equalsIgnoreCase(t.getTransactionType()) || (t.getAmount() != null && t.getAmount().compareTo(BigDecimal.ZERO) > 0 && !"WITHDRAWAL".equalsIgnoreCase(t.getTransactionType())))
                    .map(t -> t.getNormalizedAmount() != null ? t.getNormalizedAmount() : t.getAmount())
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalWithdrawals = recentTxns.stream()
                    .filter(t -> "WITHDRAWAL".equalsIgnoreCase(t.getTransactionType()) || "TRANSFER".equalsIgnoreCase(t.getTransactionType()))
                    .map(t -> t.getNormalizedAmount() != null ? t.getNormalizedAmount() : t.getAmount())
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDeposits.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = totalWithdrawals.divide(totalDeposits, 2, RoundingMode.HALF_UP);
                if (ratio.compareTo(new BigDecimal("0.80")) >= 0) {
                    triggeredRules.add("RAPID_FUND_MOVEMENT");
                    evidence.add("Transferred " + ratio.multiply(new BigDecimal("100")) + "% of total deposited funds ($" + totalDeposits + ") out within " + windowHours + "h");
                    accumulatedRiskScore += cfg != null && cfg.getRiskWeight() != null ? cfg.getRiskWeight() : 45;
                }
            }
        }

        // Rule 4: High Risk Jurisdiction
        if (ruleConfigCacheService.isRuleEnabled("HIGH_RISK_JURISDICTION")) {
            RuleConfig cfg = ruleConfigCacheService.getRuleConfig("HIGH_RISK_JURISDICTION");
            List<String> highRiskCountries = List.of("IR", "KP", "SY", "RU", "AF", "BY", "MM");
            if (txn.getCounterpartyCountry() != null && highRiskCountries.contains(txn.getCounterpartyCountry().toUpperCase())) {
                triggeredRules.add("HIGH_RISK_JURISDICTION");
                evidence.add("Counterparty country [" + txn.getCounterpartyCountry() + "] is listed on high-risk/sanctions watchlist");
                accumulatedRiskScore += cfg != null && cfg.getRiskWeight() != null ? cfg.getRiskWeight() : 60;
            }
        }

        // Rule 5: Behavioral Deviation
        if (ruleConfigCacheService.isRuleEnabled("BEHAVIORAL_DEVIATION")) {
            RuleConfig cfg = ruleConfigCacheService.getRuleConfig("BEHAVIORAL_DEVIATION");
            Optional<Account> accOpt = accountRepository.findById(txn.getAccountId());
            if (accOpt.isPresent()) {
                BigDecimal avgBal = accOpt.get().getAvgMonthlyBalance6m();
                if (avgBal != null && avgBal.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal threshold = avgBal.multiply(new BigDecimal("3.0"));
                    if (txnAmount.compareTo(threshold) > 0) {
                        triggeredRules.add("BEHAVIORAL_DEVIATION");
                        evidence.add("Transaction amount $" + txnAmount + " exceeds 3x customer average balance ($" + avgBal + ")");
                        accumulatedRiskScore += cfg != null && cfg.getRiskWeight() != null ? cfg.getRiskWeight() : 35;
                    }
                }
            }
        }

        // Generate Alert if any rule triggered
        if (!triggeredRules.isEmpty()) {
            int finalRiskScore = Math.min(100, accumulatedRiskScore);

            Alert alert = new Alert();
            alert.setAlertId("ALT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            alert.setCustomerId(txn.getCustomerId());
            alert.setAccountId(txn.getAccountId());
            alert.setRiskScore(finalRiskScore);
            alert.setStatus("NEW");
            alert.setTriggeredRule(String.join(", ", triggeredRules));
            alert.setExplanation("Suspicious activity detected triggering rules: " + String.join(", ", triggeredRules));
            alert.setEvidenceDetails(String.join(" | ", evidence) + " | Txn ID: " + txn.getTransactionId());

            Alert savedAlert = alertRepository.save(alert);
            log.warn("ALERT GENERATED: Alert ID [{}] Risk Score [{}] Customer [{}] Rules [{}]", savedAlert.getAlertId(), finalRiskScore, txn.getCustomerId(), alert.getTriggeredRule());

            // Send to Kafka Alert Topic (which automatically pushes to WebSockets for UI)
            alertProducer.sendAlert(savedAlert);
        }
    }
}
