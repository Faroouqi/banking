package com.example.finance.banking.service;

import com.example.finance.banking.dto.InsightDTO;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.repository.BudgetRepository;
import com.example.finance.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsightService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    BudgetRepository budgetRepository;

    public List<InsightDTO> generateInsights(Integer userId) {

        List<InsightDTO> insights = new ArrayList<>();

        LocalDate now = LocalDate.now();

        LocalDate startCurrentMonth = now.withDayOfMonth(1);
        LocalDate startLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate endLastMonth = now.withDayOfMonth(1).minusDays(1);

        Double currentMonthExpense = transactionRepository.getTotalByTypeAndDateRange(
                userId, Transaction.TransactionType.EXPENSE, startCurrentMonth, now);


        Double currentMonthIncome = transactionRepository.getTotalByTypeAndDateRange(
                userId, Transaction.TransactionType.INCOME, startCurrentMonth, now);

        Double lastMonthExpense = transactionRepository.getTotalByTypeAndDateRange(
                userId, Transaction.TransactionType.EXPENSE, startLastMonth, endLastMonth);

        currentMonthExpense = currentMonthExpense != null ? currentMonthExpense : 0.0;
        lastMonthExpense = lastMonthExpense != null ? lastMonthExpense : 0.0;
        currentMonthIncome = currentMonthIncome!=null ? currentMonthIncome:0.0;
        double sub = currentMonthIncome - currentMonthExpense;
        sub = sub>0?sub:0;
        String message = "This month income is ₹ " + currentMonthIncome + "\n" +
                "Expenses are ₹ " + currentMonthExpense + "\n" +
                "You have saved ₹ " + sub;
        insights.add(new InsightDTO(message ,"info"));
        if (lastMonthExpense > 0) {
            double change = ((currentMonthExpense - lastMonthExpense) / lastMonthExpense) * 100;

            if (change > 0) {
                insights.add(new InsightDTO(
                        "You spent " + Math.round(change) + "% more this month than last month.",
                        "warning"
                ));
            } else {
                insights.add(new InsightDTO(
                        "You reduced spending by " + Math.abs(Math.round(change)) + "% this month.",
                        "success"
                ));
            }
        }
        String category = transactionRepository.getTotalByTypeAndDateRangeAndCategory(
                userId, Transaction.TransactionType.EXPENSE, startCurrentMonth, now);
        insights.add(new InsightDTO(category + " is your highest spending category this month","success"));
        BigDecimal budgetAmount = budgetRepository.getTotalBudgetByUserAndMonth(userId,String.valueOf(LocalDate.now().getMonthValue()),String.valueOf(LocalDate.now().getYear()));
        BigDecimal expense = BigDecimal.valueOf(currentMonthExpense);
        if(expense.compareTo(budgetAmount) < 0)
        {
            insights.add(new InsightDTO(
                    "Great job! You stayed within your budget this month",
                    "success"
            ));
        }else{
            insights.add(new InsightDTO(
                    "Spending exceeded your budget — consider reviewing categories to balance expenses",
                    "warning"
            ));
        }
        return insights;
    }
}
