package com.example.finance.banking.service;

import com.example.finance.banking.dto.BudgetDTO;
import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final Mapper mapper;
    @Autowired
    public BudgetService(BudgetRepository budgetRepository, Mapper mapper) {
        this.budgetRepository = budgetRepository;
        this.mapper = mapper;
    }

    public Budget getById(Integer id)
    {
        return budgetRepository.findById(id).orElseThrow();
    }

    public List<BudgetDTO> getBudgetsForUser(User user) {
        List<Budget> budgets = budgetRepository.findByUser(user); // or findByUserId(user.getId())
        return budgets.stream()
                .map(budget -> mapper.toDTO(budget))
                .toList();
    }
}
