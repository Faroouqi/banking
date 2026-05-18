package com.example.finance.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanDTO {

    private Integer id;
    private Integer userId;
    private String personName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private String status;
    private LocalDate date;
    private String note;
    private LocalDateTime createdAt;

    public LoanDTO() {
    }

    public LoanDTO(
            Integer id,
            Integer userId,
            String personName,
            BigDecimal amount,
            BigDecimal paidAmount,
            String status,
            LocalDate date,
            String note,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.personName = personName;
        this.amount = amount;
        this.paidAmount = paidAmount;
        this.status = status;
        this.date = date;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}