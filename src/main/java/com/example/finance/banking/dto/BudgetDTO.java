package com.example.finance.banking.dto;

import java.math.BigDecimal;

public class BudgetDTO {
    private Integer id;
    private String category;
    private String month;  // e.g., "July"
    private String year;   // e.g., "2025"
    private BigDecimal budgetAmount;

    // Optional: frontend display / user-specific logic
    private Integer userId;

    public BudgetDTO() {}

    public BudgetDTO(Integer id, String category, String month, String year, BigDecimal budgetAmount) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.year = year;
        this.budgetAmount = budgetAmount;
    }

    public BudgetDTO(Integer id, String category, String month, String year, BigDecimal budgetAmount, Integer userId) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.year = year;
        this.budgetAmount = budgetAmount;
        this.userId = userId;
    }

    // ✅ Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
