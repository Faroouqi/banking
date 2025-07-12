package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget,Integer> {

    List<Budget> findByUser(User user);
}
