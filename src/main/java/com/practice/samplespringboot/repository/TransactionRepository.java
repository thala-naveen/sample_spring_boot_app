package com.practice.samplespringboot.repository;

import com.practice.samplespringboot.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountIdAndTimestampBetween(String accountId, LocalDateTime start, LocalDateTime end);
    
    List<Transaction> findByCustomerIdAndTimestampBetween(String customerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<Transaction> findRecentByAccount(@Param("accountId") String accountId, @Param("since") LocalDateTime since);
}
