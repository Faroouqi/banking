package com.example.finance.banking.service;

import com.example.finance.banking.dto.LoanDTO;
import com.example.finance.banking.entity.Loan;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final Mapper mapper;

    public LoanService(LoanRepository loanRepository, Mapper mapper) {
        this.loanRepository = loanRepository;
        this.mapper = mapper;
    }

    public LoanDTO saveLoan(LoanDTO loanDTO, User user)
    {
       Loan loan =  loanRepository.save(mapper.mappingLoanDTOtoLoan(loanDTO,user));
       return mapper.mappingLoantoLoanDTO(loan);
    }

    public List<LoanDTO> getLoan(Integer id)
    {
        List<Loan> loanList = loanRepository.findByUserId(id);
        List<LoanDTO> loanDTOS = new ArrayList<>();
        loanList.forEach(loan -> {
            loanDTOS.add(mapper.mappingLoantoLoanDTO(loan));
        });
        return loanDTOS;
    }

    public LoanDTO updateLoan(Integer id,Integer userId,BigDecimal amount)
    {
        Loan loan = loanRepository.findByIdAndUserId(userId,id);
        BigDecimal amo = loan.getPaidAmount();
        amo = amo.add(amount);
        if(amo.equals(loan.getAmount()))
        {
            loan.setStatus(Loan.LoanStatus.PAID);
        }
        loan.setPaidAmount(amo);
        Loan saved = loanRepository.save(loan);
        return mapper.mappingLoantoLoanDTO(saved);
    }
}
