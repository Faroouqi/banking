package com.example.finance.banking.service;

import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import java.util.*;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final Mapper mapper;

    public TransactionService(TransactionRepository transactionRepository, Mapper mapper) {
        this.transactionRepository = transactionRepository;
        this.mapper = mapper;
    }
    public List<Transaction> getByUserId(Integer id)
    {
        return transactionRepository.findByUserId(id);
    }
    public Transaction getById(Integer id)
    {
        return transactionRepository.findById(id).orElseThrow();
    }
    public TransactionDTO createTransaction(TransactionDTO dto, User user) {


        Transaction saved = transactionRepository.save(mapper.tDTOtoTransaction(dto,user));

        return mapper.mappingTransactiontoTransactionDTO(saved);
    }
    public TransactionDTO getTransactionById(Integer id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElse(null);

        log.info("After fetching");
        if (transaction == null || !transaction.getUser().getId().equals(user.getId())) {
            return null; // Return null if not owned
        }

        return mapper.mappingTransactiontoTransactionDTO(transaction);
    }
    public List<Transaction> getAll()
    {
        return transactionRepository.findAll();

    }
    public Map<Integer, BigDecimal> getSpendings()
    {
//        Map<Integer,Integer> mp = new HashMap<>();
        List<Transaction> trs = getAll();
        Map<Integer, BigDecimal> mp = new HashMap<>();
        int currentYear = LocalDate.now().getYear();

        trs.forEach(transaction -> {
            if (transaction.getDate().getYear() == currentYear) {
                int month = transaction.getDate().getMonthValue();
                mp.put(month,
                        mp.getOrDefault(month, BigDecimal.ZERO)
                                .add(transaction.getAmount()));
            }
        });
        return mp;
    }
    public List<TransactionDTO> getByMonth(int month)
    {
        List<Transaction> transactions =transactionRepository.findByYearAndMonth(Year.now().getValue(),month);
        ArrayList<TransactionDTO> transactionList = new ArrayList<>();
        transactions.forEach(transaction -> {
            transactionList.add(mapper.mappingTransactiontoTransactionDTO(transaction));
        });
        return transactionList;
    }
    public List<TransactionDTO> getByYear()
    {
        List<Transaction> transactions = transactionRepository.findByYear(Year.now().getValue());
        ArrayList<TransactionDTO> transactionList = new ArrayList<>();
        transactions.forEach(transaction -> {
            transactionList.add(mapper.mappingTransactiontoTransactionDTO(transaction));
        });
        return transactionList;
    }

    public List<TransactionDTO> getByRange(LocalDate current, LocalDate fina)
    {
        List<Transaction> transactions = transactionRepository.findByDateBetween(current,fina);
        ArrayList<TransactionDTO> transactionList = new ArrayList<>();
        transactions.forEach(transaction -> {
            transactionList.add(mapper.mappingTransactiontoTransactionDTO(transaction));
        });
        return transactionList;
    }
    public TransactionDTO updateTransaction(User user,String field,String newValue,Integer id)
    {
        Transaction transaction = transactionRepository.findById(id)
                .orElse(null);

        if(field.equals("amount"))
        {
            BigDecimal bd = new BigDecimal(newValue);
            transaction.setAmount(bd);
        }else if(field.equals("category"))
        {
            transaction.setCategory(newValue);
        }
        transaction.setDate(LocalDate.now());
        Transaction saved = transactionRepository.save(transaction);
       return mapper.mappingTransactiontoTransactionDTO(saved);

    }

    public TransactionDTO updateAmount(User user,BigDecimal amount,Integer id)
    {
        Transaction transaction = transactionRepository.findById(id)
                .orElse(null);
        BigDecimal amo = transaction.getAmount();
        log.info("amount Before adding: " + amo);

        amo = amo.add(amount);
        log.info("transaction: " + amo);
        transaction.setAmount(amo);

        log.info("amount after adding: " + transaction.getAmount());
        Transaction saved = transactionRepository.save(transaction);
        return mapper.mappingTransactiontoTransactionDTO(saved);
    }

    public void deleteTransaction(List<Integer> ids) {
        for (Integer id : ids) {
            transactionRepository.deleteById(Math.toIntExact(id));
        }

    }

    public Transaction updateCategoryTransaction(String category, User user)
    {
        log.info("Category: "+category + " User: " + user.getId());
        int month = LocalDate.now().getMonthValue();
        Transaction t = transactionRepository.findByCategoryAndUserId(category,user.getId(),month);
        log.info("Updated Transaction t: "+ t);
        return t;
    }
}
