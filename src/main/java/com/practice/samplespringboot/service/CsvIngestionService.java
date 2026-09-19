package com.practice.samplespringboot.service;

import com.practice.samplespringboot.entity.Account;
import com.practice.samplespringboot.entity.Customer;
import com.practice.samplespringboot.entity.Transaction;
import com.practice.samplespringboot.kafka.TransactionProducer;
import com.practice.samplespringboot.repository.AccountRepository;
import com.practice.samplespringboot.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CsvIngestionService {

    private static final Logger log = LoggerFactory.getLogger(CsvIngestionService.class);

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionProducer transactionProducer;

    public CsvIngestionService(CustomerRepository customerRepository,
                               AccountRepository accountRepository,
                               TransactionProducer transactionProducer) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionProducer = transactionProducer;
    }

    public int ingestCustomers(MultipartFile file) throws Exception {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return 0;
            String[] headers = headerLine.split(",");

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                Customer c = new Customer();
                c.setCustomerId(getVal(cols, headerMap, "customer_id"));
                c.setFirstName(getVal(cols, headerMap, "first_name"));
                c.setLastName(getVal(cols, headerMap, "last_name"));
                c.setGender(getVal(cols, headerMap, "gender"));
                c.setDateOfBirth(parseDate(getVal(cols, headerMap, "date_of_birth")));
                c.setAge(parseInt(getVal(cols, headerMap, "age")));
                c.setEmail(getVal(cols, headerMap, "email"));
                c.setPhoneNumber(getVal(cols, headerMap, "phone_number"));
                c.setCity(getVal(cols, headerMap, "city"));
                c.setState(getVal(cols, headerMap, "state"));
                c.setCountry(getVal(cols, headerMap, "country"));
                c.setPostalCode(getVal(cols, headerMap, "postal_code"));
                c.setOccupation(getVal(cols, headerMap, "occupation"));
                c.setAnnualIncome(parseBigDecimal(getVal(cols, headerMap, "annual_income")));
                c.setMaritalStatus(getVal(cols, headerMap, "marital_status"));
                c.setEducationLevel(getVal(cols, headerMap, "education_level"));
                c.setEmploymentStatus(getVal(cols, headerMap, "employment_status"));
                c.setCustomerSince(parseDate(getVal(cols, headerMap, "customer_since")));
                c.setCustomerSegment(getVal(cols, headerMap, "customer_segment"));
                c.setKycStatus(getVal(cols, headerMap, "kyc_status"));
                c.setRiskRating(getVal(cols, headerMap, "risk_rating"));
                c.setIsPoliticallyExposed(parseInt(getVal(cols, headerMap, "is_politically_exposed")));
                c.setPreferredChannel(getVal(cols, headerMap, "preferred_channel"));
                c.setEmailVerified(getVal(cols, headerMap, "email_verified"));
                c.setPhoneVerified(getVal(cols, headerMap, "phone_verified"));
                c.setNumComplaintsLastYear(parseInt(getVal(cols, headerMap, "num_complaints_last_year")));

                if (c.getCustomerId() != null && !c.getCustomerId().isEmpty()) {
                    customers.add(c);
                }
            }
        }
        customerRepository.saveAll(customers);
        log.info("Successfully ingested {} customers into PostgreSQL DB", customers.size());
        return customers.size();
    }

    public int ingestAccounts(MultipartFile file) throws Exception {
        List<Account> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return 0;
            String[] headers = headerLine.split(",");

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                Account a = new Account();
                a.setAccountId(getVal(cols, headerMap, "account_id"));
                a.setCustomerId(getVal(cols, headerMap, "customer_id"));
                a.setAccountType(getVal(cols, headerMap, "account_type"));
                a.setAccountStatus(getVal(cols, headerMap, "account_status"));
                a.setCurrency(getVal(cols, headerMap, "currency"));
                a.setOpenDate(parseDate(getVal(cols, headerMap, "open_date")));
                a.setCloseDate(parseDate(getVal(cols, headerMap, "close_date")));
                a.setBranchCode(getVal(cols, headerMap, "branch_code"));
                a.setBranchCity(getVal(cols, headerMap, "branch_city"));
                a.setCurrentBalance(parseBigDecimal(getVal(cols, headerMap, "current_balance")));
                a.setAvgMonthlyBalance6m(parseBigDecimal(getVal(cols, headerMap, "avg_monthly_balance_6m")));
                a.setCreditLimit(parseBigDecimal(getVal(cols, headerMap, "credit_limit")));
                a.setCreditUtilizationPct(parseBigDecimal(getVal(cols, headerMap, "credit_utilization_pct")));
                a.setOverdraftEnabled(getVal(cols, headerMap, "overdraft_enabled"));
                a.setCardType(getVal(cols, headerMap, "card_type"));
                a.setIsJointAccount(parseInt(getVal(cols, headerMap, "is_joint_account")));
                a.setNumLinkedDevices(parseInt(getVal(cols, headerMap, "num_linked_devices")));
                a.setMobileBankingEnrolled(getVal(cols, headerMap, "mobile_banking_enrolled"));
                a.setLastLoginDate(parseDate(getVal(cols, headerMap, "last_login_date")));
                a.setAvgMonthlyTxnCount(parseInt(getVal(cols, headerMap, "avg_monthly_txn_count")));
                a.setAccountTier(getVal(cols, headerMap, "account_tier"));

                if (a.getAccountId() != null && !a.getAccountId().isEmpty()) {
                    accounts.add(a);
                }
            }
        }
        accountRepository.saveAll(accounts);
        log.info("Successfully ingested {} accounts into PostgreSQL DB", accounts.size());
        return accounts.size();
    }

    public int ingestTransactions(MultipartFile file) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return 0;
            String[] headers = headerLine.split(",");

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = line.split(",", -1);
                Transaction t = new Transaction();
                t.setTransactionId(getVal(cols, headerMap, "transaction_id"));
                if (t.getTransactionId() == null || t.getTransactionId().isEmpty()) {
                    t.setTransactionId("TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                }
                t.setAccountId(getVal(cols, headerMap, "account_id"));
                t.setCustomerId(getVal(cols, headerMap, "customer_id"));
                t.setAmount(parseBigDecimal(getVal(cols, headerMap, "amount")));
                t.setCurrency(getVal(cols, headerMap, "currency"));
                t.setNormalizedAmount(parseBigDecimal(getVal(cols, headerMap, "normalized_amount")));
                if (t.getNormalizedAmount() == null) t.setNormalizedAmount(t.getAmount());
                t.setTransactionType(getVal(cols, headerMap, "transaction_type"));
                t.setTimestamp(parseDateTime(getVal(cols, headerMap, "timestamp")));
                if (t.getTimestamp() == null) t.setTimestamp(LocalDateTime.now());
                t.setCounterpartyAccountId(getVal(cols, headerMap, "counterparty_account_id"));
                t.setCounterpartyCountry(getVal(cols, headerMap, "counterparty_country"));
                t.setChannel(getVal(cols, headerMap, "channel"));
                t.setDescription(getVal(cols, headerMap, "description"));

                // Push transaction to Kafka topic for real-time detection pipeline
                transactionProducer.sendTransaction(t);
                Thread.sleep(2000);
                count++;
            }
        }
        log.info("Successfully pushed {} transactions to Kafka pipeline", count);
        return count;
    }

    private String getVal(String[] cols, Map<String, Integer> map, String key) {
        Integer idx = map.get(key);
        if (idx != null && idx < cols.length) {
            String val = cols[idx].trim();
            return val.isEmpty() ? null : val;
        }
        return null;
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isEmpty()) return null;
        try {
            return LocalDate.parse(val, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String val) {
        if (val == null || val.isEmpty()) return null;
        try {
            if (val.contains("T")) {
                return LocalDateTime.parse(val, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            return LocalDateTime.parse(val, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String val) {
        if (val == null || val.isEmpty()) return null;
        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String val) {
        if (val == null || val.isEmpty()) return null;
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return null;
        }
    }
}
