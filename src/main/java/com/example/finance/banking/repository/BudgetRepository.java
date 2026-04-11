package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Budget;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget,Integer> {

    List<Budget> findByUser(User user);
    List<Budget> findByMonthAndYear(String month, String year);

    @Query("SELECT COALESCE(SUM(t.budgetAmount), 0) FROM Budget t " +
            "WHERE t.user.id = :userId " +
            "AND t.month = :month " +
            "AND t.year = :year")
    BigDecimal getTotalBudgetByUserAndMonth(@Param("userId") Integer userId,
                                            @Param("month") String month,
                                            @Param("year") String year);
}
