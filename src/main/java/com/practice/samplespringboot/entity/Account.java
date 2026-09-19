package com.practice.samplespringboot.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_id")
    private String accountId;

    private String customerId;
    private String accountType;
    private String accountStatus;
    private String currency;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String branchCode;
    private String branchCity;
    private BigDecimal currentBalance;

    @Column(name = "avg_monthly_balance_6m")
    private BigDecimal avgMonthlyBalance6m;
    private BigDecimal creditLimit;
    private BigDecimal creditUtilizationPct;
    private String overdraftEnabled;
    private String cardType;
    private Integer isJointAccount;
    private Integer numLinkedDevices;
    private String mobileBankingEnrolled;
    private LocalDate lastLoginDate;
    private Integer avgMonthlyTxnCount;
    private String accountTier;

    public Account() {}

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getOpenDate() { return openDate; }
    public void setOpenDate(LocalDate openDate) { this.openDate = openDate; }

    public LocalDate getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }

    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchCity() { return branchCity; }
    public void setBranchCity(String branchCity) { this.branchCity = branchCity; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public BigDecimal getAvgMonthlyBalance6m() { return avgMonthlyBalance6m; }
    public void setAvgMonthlyBalance6m(BigDecimal avgMonthlyBalance6m) { this.avgMonthlyBalance6m = avgMonthlyBalance6m; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public BigDecimal getCreditUtilizationPct() { return creditUtilizationPct; }
    public void setCreditUtilizationPct(BigDecimal creditUtilizationPct) { this.creditUtilizationPct = creditUtilizationPct; }

    public String getOverdraftEnabled() { return overdraftEnabled; }
    public void setOverdraftEnabled(String overdraftEnabled) { this.overdraftEnabled = overdraftEnabled; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public Integer getIsJointAccount() { return isJointAccount; }
    public void setIsJointAccount(Integer isJointAccount) { this.isJointAccount = isJointAccount; }

    public Integer getNumLinkedDevices() { return numLinkedDevices; }
    public void setNumLinkedDevices(Integer numLinkedDevices) { this.numLinkedDevices = numLinkedDevices; }

    public String getMobileBankingEnrolled() { return mobileBankingEnrolled; }
    public void setMobileBankingEnrolled(String mobileBankingEnrolled) { this.mobileBankingEnrolled = mobileBankingEnrolled; }

    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public Integer getAvgMonthlyTxnCount() { return avgMonthlyTxnCount; }
    public void setAvgMonthlyTxnCount(Integer avgMonthlyTxnCount) { this.avgMonthlyTxnCount = avgMonthlyTxnCount; }

    public String getAccountTier() { return accountTier; }
    public void setAccountTier(String accountTier) { this.accountTier = accountTier; }
}
