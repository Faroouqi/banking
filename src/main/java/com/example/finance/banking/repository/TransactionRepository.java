package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findByUserId(Integer id);


}
