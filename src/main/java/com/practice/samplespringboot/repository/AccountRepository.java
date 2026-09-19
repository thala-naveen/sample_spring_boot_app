package com.practice.samplespringboot.repository;

import com.practice.samplespringboot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
}
