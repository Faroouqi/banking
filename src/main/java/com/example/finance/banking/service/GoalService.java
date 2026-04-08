package com.example.finance.banking.service;

import com.example.finance.banking.dto.GoalDTO;
import com.example.finance.banking.entity.Goal;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.enu.GoalStatus;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public GoalDTO getGoal(User user, String name, BigDecimal amount) {
        List<Goal> goals = goalRepository.findByUser_Id(user.getId());
        if (goals.isEmpty()) {
            throw new RuntimeException("Goal is not present for User: " + user.getId());
        }
        List<Goal> laptopGoals = goals.stream()
                .filter(goal -> name.equalsIgnoreCase(goal.getGoalName()))
                .toList();
        Goal goal = laptopGoals.get(0);
        BigDecimal remaingAmount = goal.getTargetAmount().subtract(goal.getSavedAmount());
        if (amount.compareTo(remaingAmount) >= 0) {
            goal.setSavedAmount(goal.getTargetAmount());
            goal.setStatus(GoalStatus.ACHIEVED);
        } else {
            goal.setSavedAmount(goal.getSavedAmount().add(amount));
        }
        Goal saved = goalRepository.save(goal);
        return mapper.mappingGoaltoGoalDTO(goal);
    }

}
