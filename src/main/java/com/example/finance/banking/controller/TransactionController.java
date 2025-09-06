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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.time.Year;

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

    @GetMapping("/transactions/search")
    public ResponseEntity<?> searchTransaction(
            @RequestParam(value = "id", required = true) int id,
            @RequestParam(value = "date", required = false) Integer date,
            @RequestParam(value = "enddate", required = false) Integer enddate) {
            log.info("Transaction id is {}",id);
        if (id == 1) {
            // date is expected as month number
            if (date == null) {
                return ResponseEntity.badRequest().body("Parameter 'date' is required for id=1");
            }
            return ResponseEntity.ok(transactionService.getByMonth(date));
        } else if (id == 2) {
            return ResponseEntity.ok(transactionService.getByYear());
        } else {
            // Use default months if date or enddate is null
            int startMonth = (date != null) ? date : 1;     // default to January
            int endMonth = (enddate != null) ? enddate : 12; // default to December
            int currentYear = Year.now().getValue();

            LocalDate startDate = YearMonth.of(currentYear, startMonth).atDay(1);
            LocalDate endDate = YearMonth.of(currentYear, endMonth).atEndOfMonth();

            return ResponseEntity.ok(transactionService.getByRange(startDate, endDate));
        }
    }

}
