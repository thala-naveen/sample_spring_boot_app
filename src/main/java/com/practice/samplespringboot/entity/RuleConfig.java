package com.practice.samplespringboot.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rule_configs")
public class RuleConfig {

    @Id
    @Column(name = "rule_name")
    private String ruleName;

    private String description;
    private Boolean enabled;
    private BigDecimal thresholdAmount;
    private Integer timeWindowHours;
    private Integer riskWeight;

    @Column(columnDefinition = "TEXT")
    private String parametersJson;

    private LocalDateTime updatedAt;

    public RuleConfig() {}

    public RuleConfig(String ruleName, String description, Boolean enabled, BigDecimal thresholdAmount, Integer timeWindowHours, Integer riskWeight, String parametersJson) {
        this.ruleName = ruleName;
        this.description = description;
        this.enabled = enabled;
        this.thresholdAmount = thresholdAmount;
        this.timeWindowHours = timeWindowHours;
        this.riskWeight = riskWeight;
        this.parametersJson = parametersJson;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public BigDecimal getThresholdAmount() { return thresholdAmount; }
    public void setThresholdAmount(BigDecimal thresholdAmount) { this.thresholdAmount = thresholdAmount; }

    public Integer getTimeWindowHours() { return timeWindowHours; }
    public void setTimeWindowHours(Integer timeWindowHours) { this.timeWindowHours = timeWindowHours; }

    public Integer getRiskWeight() { return riskWeight; }
    public void setRiskWeight(Integer riskWeight) { this.riskWeight = riskWeight; }

    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
