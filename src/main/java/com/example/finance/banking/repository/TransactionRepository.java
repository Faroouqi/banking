package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findByUserId(Integer id);

    @Query("SELECT t FROM Transaction t WHERE FUNCTION('YEAR', t.date) = :year AND FUNCTION('MONTH', t.date) = :month")
    List<Transaction> findByYearAndMonth(@Param("year") int year, @Param("month") int month);


    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);


    @Query("SELECT t FROM Transaction t WHERE FUNCTION('YEAR', t.date) = :year")
    List<Transaction> findByYear(@Param("year") int year);

    @Query("SELECT t FROM Transaction t WHERE t.category = :category AND t.user.id = :userId AND FUNCTION('MONTH', t.date) = :month" )
    Transaction findByCategoryAndUserId(@Param("category") String category, @Param("userId") Integer userId,@Param("month") int month);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate")
    Double getTotalByTypeAndDateRange(
            @Param("userId") Integer userId,
            @Param("type") Transaction.TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t.category FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "AND t.amount = (" +
            "   SELECT MAX(t1.amount) FROM Transaction t1 " +
            "   WHERE t1.user.id = :userId " +
            "   AND t1.type = :type " +
            "   AND t1.date BETWEEN :startDate AND :endDate" +
            ")")
    String getTotalByTypeAndDateRangeAndCategory(@Param("userId") Integer userId,
                                                 @Param("type") Transaction.TransactionType type,
                                                 @Param("startDate") LocalDate startCurrentMonth,
                                                 @Param("endDate") LocalDate now);

    @Query(value = """
    SELECT MONTH(t.date),
        COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)
    FROM transactions t
    WHERE t.user_id = :userId
      AND t.date < :startOfCurrentMonth
    GROUP BY MONTH(t.date)
    ORDER BY MONTH(t.date)
    """, nativeQuery = true)
    List<Object[]> getMonthlySummaryTillLastMonth(
            @Param("userId") Integer userId,
            @Param("startOfCurrentMonth") LocalDate startOfCurrentMonth
    );
}


