package com.practice.samplespringboot.repository;

import com.practice.samplespringboot.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    List<Alert> findAllByOrderByRiskScoreDescCreatedAtDesc();
    
    List<Alert> findByStatusOrderByRiskScoreDesc(String status);

    List<Alert> findByCustomerId(String customerId);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = 'NEW'")
    long countNewAlerts();
}
