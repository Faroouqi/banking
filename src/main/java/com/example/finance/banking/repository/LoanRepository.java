package com.example.finance.banking.repository;

import com.example.finance.banking.entity.Loan;
import com.example.finance.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan,Integer> {
    @Query("SELECT t FROM Loan t " +
            "WHERE t.user.id = :userId ")
    List<Loan> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT t FROM Loan t " +
            "WHERE t.user.id = :userId " +
            "AND t.id = :id ")
    Loan findByIdAndUserId(@Param("userId") Integer userId,@Param("id") Integer id);

}
