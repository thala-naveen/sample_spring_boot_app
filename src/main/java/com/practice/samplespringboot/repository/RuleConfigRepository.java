package com.practice.samplespringboot.repository;

import com.practice.samplespringboot.entity.RuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleConfigRepository extends JpaRepository<RuleConfig, String> {
}
