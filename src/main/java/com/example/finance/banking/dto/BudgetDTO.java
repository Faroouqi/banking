package com.example.finance.banking.dto;

import java.math.BigDecimal;

public class BudgetDTO {
    private Integer id;
    private String category;
    private Integer month; // Typically YYYYMM or just MM if filtered by year
    private BigDecimal budgetAmount;

    // Optional: You can include userId if you want to show/edit ownership in frontend
    private Integer userId;

    public BudgetDTO() {}

    public BudgetDTO(Integer id, String category, Integer month, BigDecimal budgetAmount) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.budgetAmount = budgetAmount;
    }

    public BudgetDTO(Integer id, String category, Integer month, BigDecimal budgetAmount, Integer userId) {
        this.id = id;
        this.category = category;
        this.month = month;
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
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
