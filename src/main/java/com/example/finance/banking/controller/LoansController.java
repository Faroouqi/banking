package com.example.finance.banking.controller;

import com.example.finance.banking.dto.LoanDTO;
import com.example.finance.banking.entity.User;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.service.GoalService;
import com.example.finance.banking.service.LoanService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/loan/")
@Slf4j
public class LoansController {

    private final UserService userService;
    private final GoalService goalService;
    private Mapper mapper;
    private final UserDetailUtil util;
    private final LoanService loanService;

    public LoansController(UserService userService, GoalService goalService, UserDetailUtil util, LoanService loanService) {
        this.userService = userService;
        this.goalService = goalService;
        this.util = util;
        this.loanService = loanService;
    }

    @PostMapping("add")
    public ResponseEntity<?> addLoan(@RequestBody LoanDTO loanDTO)
    {
        if(util.getUser()==null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");
        }
        User user = util.getUser();
        LoanDTO dto  = loanService.saveLoan(loanDTO,user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("get")
    public ResponseEntity<?> getLoan()
    {
        if(util.getUser()==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");

        User user = util.getUser();
        List<LoanDTO> loanDTOS = loanService.getLoan(user.getId());
        log.info("after getting " + loanDTOS);
        return ResponseEntity.ok(loanDTOS);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Integer id, @RequestParam BigDecimal amount)
    {
        if(util.getUser()==null)  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");

        Integer userId = util.getUser().getId();
        LoanDTO loanDTO = loanService.updateLoan(id,userId,amount);
        return ResponseEntity.ok(loanDTO);
    }

}
