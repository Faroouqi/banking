package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal,Integer> {
    @Override
    List<Goal> findAll();

    List<Goal> findByUser_Id(Integer id);
}
