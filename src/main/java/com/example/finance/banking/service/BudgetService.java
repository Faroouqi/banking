package com.example.finance.banking.service;

import com.example.finance.banking.dto.BudgetDTO;
import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
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
        int currentYear = Year.now().getValue();
        List<Budget> budgets=budgetRepository.findByMonthAndYear(id,Integer.toString(currentYear));
        return budgets.stream()
                .map(budget -> mapper.toDTO(budget))
                .toList();
    }

    public List<BudgetDTO> getBudgetsForUser(User user) {
        int year = LocalDate.now().getYear();

        List<Budget> budgets = budgetRepository.findByUserAndYear(user.getId(),String.valueOf(year)); // or findByUserId(user.getId())
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
