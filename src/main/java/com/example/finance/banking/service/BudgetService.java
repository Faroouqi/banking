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

    public List<BudgetDTO> getById(String id)
    {
        List<Budget> budgets=budgetRepository.findByYear(id);
        return budgets.stream()
                .map(budget -> mapper.toDTO(budget))
                .toList();
    }

    public List<BudgetDTO> getBudgetsForUser(User user) {
        List<Budget> budgets = budgetRepository.findByUser(user); // or findByUserId(user.getId())
        return budgets.stream()
                .map(budget -> mapper.toDTO(budget))
                .toList();
    }
    public BudgetDTO addBudget(BudgetDTO dto,User user)
    {
        Budget budget = budgetRepository.save(mapper.toBudget(dto,user));
        return mapper.toDTO(budget);
    }
}
