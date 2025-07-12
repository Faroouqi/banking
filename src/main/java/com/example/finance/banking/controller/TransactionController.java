package com.example.finance.banking.controller;

import com.example.finance.banking.dto.TransactionDTO;
import com.example.finance.banking.entity.Transaction;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.service.TransactionService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class TransactionController {

        private final TransactionService transactionService;
        private final UserService userService;
        private Mapper mapper;
        private final UserDetailUtil util;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService, UserDetailUtil util,Mapper mapper) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.util = util;
        this.mapper = mapper;
    }

    @GetMapping("/transaction")
    public ResponseEntity<?> getAllTransaction()
    {
        log.info("---Getting all transaction---");
        if(util.getUser()!=null)
        {
            List<Transaction> transactions = transactionService.getAll();
            ArrayList<TransactionDTO> transactionList = new ArrayList<>();
            transactions.forEach(transaction -> {
                transactionList.add(mapper.mappingTransactiontoTransactionDTO(transaction));
            });
            return ResponseEntity.ok(transactionList);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO request) {
        log.info("--Inside CreateTransaction----");
        if (util.getUser() != null) {
            log.info("user id: {}",util.getUser().getEmail());

            return ResponseEntity.ok(transactionService.createTransaction(request,util.getUser()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");
    }
    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Integer id) {
        log.info("--Fetching Transaction info using id: {}",id);
        TransactionDTO transactionDTO = transactionService.getTransactionById(id, util.getUser());
        if (transactionDTO == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to access this transaction");
        }

        return ResponseEntity.ok(transactionDTO);
    }
}
