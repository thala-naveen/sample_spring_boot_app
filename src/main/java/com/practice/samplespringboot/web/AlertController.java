package com.practice.samplespringboot.web;

import com.practice.samplespringboot.entity.Alert;
import com.practice.samplespringboot.entity.Customer;
import com.practice.samplespringboot.repository.AlertRepository;
import com.practice.samplespringboot.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Alert & Case Management API", description = "Endpoints for alert queue monitoring, analyst dispositions, and dashboard metrics")
public class AlertController {

    private final AlertRepository alertRepository;
    private final CustomerRepository customerRepository;

    public AlertController(AlertRepository alertRepository, CustomerRepository customerRepository) {
        this.alertRepository = alertRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get Alert Queue", description = "Fetches prioritized alerts sorted by risk score. Supports status filtering and PII masking.")
    public ResponseEntity<List<Map<String, Object>>> getAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "true") boolean maskPII) {

        List<Alert> alerts = (status != null && !status.isEmpty())
                ? alertRepository.findByStatusOrderByRiskScoreDesc(status)
                : alertRepository.findAllByOrderByRiskScoreDescCreatedAtDesc();

        List<Map<String, Object>> response = new ArrayList<>();
        for (Alert alert : alerts) {
            Map<String, Object> map = new HashMap<>();
            map.put("alertId", alert.getAlertId());
            map.put("customerId", alert.getCustomerId());
            map.put("accountId", alert.getAccountId());
            map.put("riskScore", alert.getRiskScore());
            map.put("status", alert.getStatus());
            map.put("triggeredRule", alert.getTriggeredRule());
            map.put("explanation", alert.getExplanation());
            map.put("evidenceDetails", alert.getEvidenceDetails());
            map.put("createdAt", alert.getCreatedAt());
            map.put("updatedAt", alert.getUpdatedAt());
            map.put("dispositionReason", alert.getDispositionReason());
            map.put("analystId", alert.getAnalystId());

            // Attach customer details with optional PII masking
            Optional<Customer> cOpt = customerRepository.findById(alert.getCustomerId());
            if (cOpt.isPresent()) {
                Customer c = cOpt.get();
                if (maskPII) {
                    map.put("customerName", maskString(c.getFirstName()) + " " + maskString(c.getLastName()));
                    map.put("customerEmail", maskEmail(c.getEmail()));
                } else {
                    map.put("customerName", c.getFirstName() + " " + c.getLastName());
                    map.put("customerEmail", c.getEmail());
                }
                map.put("customerRiskRating", c.getRiskRating());
                map.put("kycStatus", c.getKycStatus());
            }

            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alerts/{id}")
    @Operation(summary = "Get Alert Detail", description = "Retrieves full alert details with unmasked customer PII and complete evidence breakdown")
    public ResponseEntity<Map<String, Object>> getAlertDetail(@PathVariable String id) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Alert alert = alertOpt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("alert", alert);

        Optional<Customer> cOpt = customerRepository.findById(alert.getCustomerId());
        cOpt.ifPresent(customer -> map.put("customer", customer));

        return ResponseEntity.ok(map);
    }

    @PutMapping("/alerts/{id}/disposition")
    @Operation(summary = "Submit Alert Disposition", description = "Updates alert status (e.g., CONFIRMED_SUSPICIOUS, FALSE_POSITIVE) with analyst notes and ID")
    public ResponseEntity<Alert> updateDisposition(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {

        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Alert alert = alertOpt.get();
        if (payload.containsKey("status")) alert.setStatus(payload.get("status"));
        if (payload.containsKey("reason")) alert.setDispositionReason(payload.get("reason"));
        if (payload.containsKey("analystId")) alert.setAnalystId(payload.get("analystId"));

        Alert updated = alertRepository.save(alert);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get Dashboard Analytics", description = "Returns aggregated metrics for alert volume, risk levels, and status breakdown")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<Alert> allAlerts = alertRepository.findAll();
        long totalAlerts = allAlerts.size();
        long newAlerts = allAlerts.stream().filter(a -> "NEW".equalsIgnoreCase(a.getStatus())).count();
        long suspiciousCount = allAlerts.stream().filter(a -> "CONFIRMED_SUSPICIOUS".equalsIgnoreCase(a.getStatus())).count();
        long falsePositives = allAlerts.stream().filter(a -> "FALSE_POSITIVE".equalsIgnoreCase(a.getStatus())).count();
        long highRiskCount = allAlerts.stream().filter(a -> a.getRiskScore() != null && a.getRiskScore() >= 70).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlerts", totalAlerts);
        stats.put("newAlerts", newAlerts);
        stats.put("confirmedSuspicious", suspiciousCount);
        stats.put("falsePositives", falsePositives);
        stats.put("highRiskAlerts", highRiskCount);

        return ResponseEntity.ok(stats);
    }

    private String maskString(String str) {
        if (str == null || str.isEmpty()) return "***";
        if (str.length() <= 2) return str.charAt(0) + "*";
        return str.charAt(0) + "*".repeat(str.length() - 2) + str.charAt(str.length() - 1);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****@****.com";
        String[] parts = email.split("@");
        return maskString(parts[0]) + "@" + parts[1];
    }
}
