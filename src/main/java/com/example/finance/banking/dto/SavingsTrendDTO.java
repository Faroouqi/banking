package com.example.finance.banking.dto;

import java.util.List;

public class SavingsTrendDTO {

    private Double totalSavingsTillLastMonth;
    private List<MonthlySummaryDTO> monthlySummary;

    public Double getTotalSavingsTillLastMonth() {
        return totalSavingsTillLastMonth;
    }

    public void setTotalSavingsTillLastMonth(Double totalSavingsTillLastMonth) {
        this.totalSavingsTillLastMonth = totalSavingsTillLastMonth;
    }

    public List<MonthlySummaryDTO> getMonthlySummary() {
        return monthlySummary;
    }

    public void setMonthlySummary(List<MonthlySummaryDTO> monthlySummary) {
        this.monthlySummary = monthlySummary;
    }

    public SavingsTrendDTO() {}

    public SavingsTrendDTO(Double totalSavingsTillLastMonth,
                           List<MonthlySummaryDTO> monthlySummary) {
        this.totalSavingsTillLastMonth = totalSavingsTillLastMonth;
        this.monthlySummary = monthlySummary;
    }

    // getters setters
}