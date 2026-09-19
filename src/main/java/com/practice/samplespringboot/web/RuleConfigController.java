package com.practice.samplespringboot.web;

import com.practice.samplespringboot.entity.RuleConfig;
import com.practice.samplespringboot.service.RuleConfigCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/rules")
@Tag(name = "Rule Configuration API", description = "Endpoints for viewing and dynamically tuning AML detection rules at runtime")
public class RuleConfigController {

    private final RuleConfigCacheService ruleConfigCacheService;

    public RuleConfigController(RuleConfigCacheService ruleConfigCacheService) {
        this.ruleConfigCacheService = ruleConfigCacheService;
    }

    @GetMapping
    @Operation(summary = "Get all rule configurations", description = "Retrieves current active configuration and thresholds for all AML detection rules")
    public ResponseEntity<Collection<RuleConfig>> getAllRules() {
        return ResponseEntity.ok(ruleConfigCacheService.getAllRules());
    }

    @GetMapping("/{ruleName}")
    @Operation(summary = "Get rule configuration by name")
    public ResponseEntity<RuleConfig> getRuleByName(@PathVariable String ruleName) {
        RuleConfig rule = ruleConfigCacheService.getRuleConfig(ruleName);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{ruleName}")
    @Operation(summary = "Update rule configuration dynamically", description = "Modifies thresholds, time windows, risk weights, or toggles rule enabled status at runtime")
    public ResponseEntity<RuleConfig> updateRule(@PathVariable String ruleName, @RequestBody RuleConfig updatedConfig) {
        RuleConfig rule = ruleConfigCacheService.updateRule(ruleName, updatedConfig);
        return ResponseEntity.ok(rule);
    }
}
