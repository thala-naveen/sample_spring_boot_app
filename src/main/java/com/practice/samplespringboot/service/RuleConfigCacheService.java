package com.practice.samplespringboot.service;

import com.practice.samplespringboot.entity.RuleConfig;
import com.practice.samplespringboot.repository.RuleConfigRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleConfigCacheService {

    private static final Logger log = LoggerFactory.getLogger(RuleConfigCacheService.class);

    private final RuleConfigRepository ruleConfigRepository;
    private final Map<String, RuleConfig> cache = new ConcurrentHashMap<>();

    public RuleConfigCacheService(RuleConfigRepository ruleConfigRepository) {
        this.ruleConfigRepository = ruleConfigRepository;
    }

    @PostConstruct
    public void init() {
        if (ruleConfigRepository.count() == 0) {
            log.info("Initializing default AML Detection Rules in PostgreSQL DB...");
            List<RuleConfig> defaultRules = List.of(
                new RuleConfig("LARGE_TRANSACTION", "CTR Threshold: Flag single transaction >= $10,000", true, new BigDecimal("10000.00"), 0, 40, "{\"currency\":\"USD\"}"),
                new RuleConfig("STRUCTURING", "Smurfing: 3+ transactions between $9,000-$9,999 in 24h", true, new BigDecimal("9000.00"), 24, 50, "{\"maxThreshold\":9999.99, \"minCount\":3}"),
                new RuleConfig("RAPID_FUND_MOVEMENT", "Layering: >=80% deposited funds transferred out within 48h", true, new BigDecimal("0.80"), 48, 45, "{\"percentageOut\":80}"),
                new RuleConfig("HIGH_RISK_JURISDICTION", "Sanctions: Transfer involving high-risk/sanctioned country", true, new BigDecimal("0.00"), 0, 60, "{\"highRiskCountries\":[\"IR\",\"KP\",\"SY\",\"RU\",\"AF\",\"BY\",\"MM\"]}"),
                new RuleConfig("BEHAVIORAL_DEVIATION", "Volume Spike: Transaction exceeds 3x 90-day daily average", true, new BigDecimal("3.00"), 2160, 35, "{\"multiplier\":3.0}")
            );
            ruleConfigRepository.saveAll(defaultRules);
        }
        refreshCache();
    }

    @Scheduled(fixedRate = 10000)
    public void pollRuleConfigs() {
        log.debug("Polling RuleConfig table from PostgreSQL DB (10s interval)...");
        refreshCache();
    }

    public synchronized void refreshCache() {
        List<RuleConfig> rules = ruleConfigRepository.findAll();
        for (RuleConfig rule : rules) {
            cache.put(rule.getRuleName(), rule);
        }
        log.info("RuleConfig cache refreshed with {} active rules", cache.size());
    }

    public RuleConfig getRuleConfig(String ruleName) {
        return cache.get(ruleName);
    }

    public boolean isRuleEnabled(String ruleName) {
        RuleConfig rule = cache.get(ruleName);
        return rule != null && Boolean.TRUE.equals(rule.getEnabled());
    }

    public Collection<RuleConfig> getAllRules() {
        return cache.values();
    }

    public RuleConfig updateRule(String ruleName, RuleConfig updatedConfig) {
        updatedConfig.setRuleName(ruleName);
        RuleConfig saved = ruleConfigRepository.save(updatedConfig);
        cache.put(ruleName, saved);
        log.info("RuleConfig [{}] updated live and refreshed in cache", ruleName);
        return saved;
    }
}
