package com.example.finance.banking.service;

import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
