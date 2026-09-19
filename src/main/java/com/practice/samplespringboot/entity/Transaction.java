package com.practice.samplespringboot.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    private String accountId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private BigDecimal normalizedAmount; // Amount in USD / base currency
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
    private LocalDateTime timestamp;
    private String counterpartyAccountId;
    private String counterpartyCountry;
    private String channel; // ONLINE, ATM, WIRE, BRANCH
    private String description;

    public Transaction() {}

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getNormalizedAmount() { return normalizedAmount; }
    public void setNormalizedAmount(BigDecimal normalizedAmount) { this.normalizedAmount = normalizedAmount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCounterpartyAccountId() { return counterpartyAccountId; }
    public void setCounterpartyAccountId(String counterpartyAccountId) { this.counterpartyAccountId = counterpartyAccountId; }

    public String getCounterpartyCountry() { return counterpartyCountry; }
    public void setCounterpartyCountry(String counterpartyCountry) { this.counterpartyCountry = counterpartyCountry; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
