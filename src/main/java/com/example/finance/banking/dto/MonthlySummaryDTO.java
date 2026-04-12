package com.example.finance.banking.dto;
public class MonthlySummaryDTO {

    private String month;
    private Double income;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }

    private Double expense;
    private Double savings;

    public MonthlySummaryDTO() {}

    public MonthlySummaryDTO(String month, Double income, Double expense, Double savings) {
        this.month = month;
        this.income = income;
        this.expense = expense;
        this.savings = savings;
    }

    // getters setters
}
