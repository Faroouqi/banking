package com.example.finance.banking.mapper;

import com.example.finance.banking.dto.BudgetDTO;
import com.example.finance.banking.dto.GoalDTO;
import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.dto.UserRequestDTO;
import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.Goal;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.enu.GoalStatus;
import org.hibernate.annotations.Comment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public Mapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User mappingUsertoUserDTO(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        return user;
    }

    public TransactionDTO mappingTransactiontoTransactionDTO(Transaction transaction)
    {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setCategory(transaction.getCategory());
        transactionDTO.setId(transaction.getId());
        transactionDTO.setDate(transaction.getDate());
        transactionDTO.setNote(transaction.getNote());
        transactionDTO.setType(transaction.getType().name());
        transactionDTO.setCreatedAt(transaction.getCreatedAt());
        transactionDTO.setUserId(transaction.getUser().getId());

        return transactionDTO;
    }
    public Transaction tDTOtoTransaction(TransactionDTO dto,User user)
    {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.valueOf(dto.getType().toUpperCase()));
        transaction.setCategory(dto.getCategory());
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setNote(dto.getNote());

        return transaction;
    }
    public  BudgetDTO toDTO(Budget budget) {
        return new BudgetDTO(
                budget.getId(),
                budget.getCategory(),
                budget.getMonth(),
                budget.getYear(),
                budget.getBudgetAmount()
        );
    }
    public Budget toBudget(BudgetDTO budgetDTO,User user)
    {
        Budget dto = new Budget();
        dto.setBudgetAmount(budgetDTO.getBudgetAmount());
        dto.setCategory(budgetDTO.getCategory());
        dto.setUser(user);
        dto.setMonth(budgetDTO.getMonth());
        dto.setYear(budgetDTO.getYear());
        return dto;
    }

    public Goal toGoal(GoalDTO goalDTO,User user)
    {
        Goal goal = new Goal();
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setUser(user);
        goal.setGoalName(goalDTO.getGoalName());
        goal.setTargetDate(goalDTO.getTargetDate());
        goal.setCreatedAt(LocalDateTime.now());
        goal.setSavedAmount(BigDecimal.valueOf(0));
        goal.setStatus(GoalStatus.ACTIVE);
        return goal;
    }

    public GoalDTO mappingGoaltoGoalDTO(Goal goal)
    {
        GoalDTO dTO = new GoalDTO();
        dTO.setId(Long.valueOf(goal.getId()));
        dTO.setUserId(Long.valueOf(goal.getUser().getId()));
        dTO.setCreatedAt(LocalDateTime.now());
        dTO.setGoalName(goal.getGoalName());
        dTO.setSavedAmount(goal.getSavedAmount());
        dTO.setTargetAmount(goal.getTargetAmount());
        dTO.setTargetDate(goal.getTargetDate());
        dTO.setStatus(goal.getStatus().getValue());
        return dTO;
    }



}
