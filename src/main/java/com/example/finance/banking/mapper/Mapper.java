package com.example.finance.banking.mapper;

import com.example.finance.banking.dto.BudgetDTO;
import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.dto.UserRequestDTO;
import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import org.hibernate.annotations.Comment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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



}
