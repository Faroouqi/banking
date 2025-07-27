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
}

