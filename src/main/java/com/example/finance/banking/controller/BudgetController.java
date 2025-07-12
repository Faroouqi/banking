package com.example.finance.banking.controller;

import com.example.finance.banking.dto.BudgetDTO;
import com.example.finance.banking.service.BudgetService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class BudgetController  {
    private final BudgetService budgetService;
    private final UserService userService;
    private final UserDetailUtil util;

    @Autowired
    public BudgetController(BudgetService budgetService, UserService userService, UserDetailUtil util) {
        this.budgetService = budgetService;
        this.userService = userService;
        this.util = util;
    }

    @GetMapping("/budgets")
    public ResponseEntity<?> getAllbudgets()
    {
        List<BudgetDTO> budgetDTOS = budgetService.getBudgetsForUser(util.getUser());
        return ResponseEntity.ok(budgetDTOS);
    }

    @GetMapping("/budgets/{id}")
    public ResponseEntity<?> getBudgetsById(@PathVariable Integer id)
    {
        return ResponseEntity.ok(budgetService.getById(id));
    }
}
