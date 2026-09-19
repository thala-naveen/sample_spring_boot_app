package com.practice.samplespringboot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @Column(name = "alert_id")
    private String alertId;

    private String customerId;
    private String accountId;
    private Integer riskScore; // 0 - 100
    private String status; // NEW, IN_REVIEW, CONFIRMED_SUSPICIOUS, FALSE_POSITIVE, CLOSED
    private String triggeredRule;
    
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @Column(columnDefinition = "TEXT")
    private String evidenceDetails; // JSON or string list of transaction IDs

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String dispositionReason;
    private String analystId;

    public Alert() {}

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTriggeredRule() { return triggeredRule; }
    public void setTriggeredRule(String triggeredRule) { this.triggeredRule = triggeredRule; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getEvidenceDetails() { return evidenceDetails; }
    public void setEvidenceDetails(String evidenceDetails) { this.evidenceDetails = evidenceDetails; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getDispositionReason() { return dispositionReason; }
    public void setDispositionReason(String dispositionReason) { this.dispositionReason = dispositionReason; }

    public String getAnalystId() { return analystId; }
    public void setAnalystId(String analystId) { this.analystId = analystId; }
}
