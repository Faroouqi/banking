package com.example.finance.banking.dto;

import com.example.finance.banking.enu.GoalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class GoalDTO {



        private Long id;
        private Long userId;
        private String goalName;
        private BigDecimal targetAmount;
        private BigDecimal savedAmount;
        private LocalDate targetDate;
        private GoalStatus status;
        private LocalDateTime createdAt;

        // Constructors
        public GoalDTO() {}

        public GoalDTO(Long id, Long userId, String goalName,
                              BigDecimal targetAmount, BigDecimal savedAmount,
                              LocalDate targetDate, String status,
                              LocalDateTime createdAt) {
            this.id = id;
            this.userId = userId;
            this.goalName = goalName;
            this.targetAmount = targetAmount;
            this.savedAmount = savedAmount;
            this.targetDate = targetDate;
            this.status = GoalStatus.ACTIVE;
            this.createdAt = createdAt;
        }

        // Getters & Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
        }

        public BigDecimal getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(BigDecimal targetAmount) {
            this.targetAmount = targetAmount;
        }

        public BigDecimal getSavedAmount() {
            return savedAmount;
        }

        public void setSavedAmount(BigDecimal savedAmount) {
            this.savedAmount = savedAmount;
        }

        public LocalDate getTargetDate() {
            return targetDate;
        }

        public void setTargetDate(LocalDate targetDate) {
            this.targetDate = targetDate;
        }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = GoalStatus.valueOf(status.toUpperCase());
    }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
