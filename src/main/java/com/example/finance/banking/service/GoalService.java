package com.example.finance.banking.service;

import com.example.finance.banking.dto.GoalDTO;
import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.entity.Goal;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.GoalRepository;
import org.springframework.stereotype.Service;

@Service
public class GoalService {

    private final Mapper mapper;
    private final GoalRepository goalRepository;

    public GoalService(Mapper mapper, GoalRepository goalRepository) {
        this.mapper = mapper;
        this.goalRepository = goalRepository;
    }

    public GoalDTO addGoal(GoalDTO dto, User user) {


        Goal saved = goalRepository.save(mapper.toGoal(dto,user));
        return mapper.mappingGoaltoGoalDTO(saved);
    }

}
