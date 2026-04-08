package com.example.finance.banking.controller;

import com.example.finance.banking.dto.GoalDTO;
import com.example.finance.banking.service.GoalService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/goals")
@Slf4j
public class GoalController {

    private final UserService userService;
    private final UserDetailUtil util;
    private final GoalService goalService;

    @Autowired
    public GoalController(UserService userService, UserDetailUtil util, GoalService goalService) {
        this.userService = userService;
        this.util = util;
        this.goalService = goalService;
    }

    @PostMapping("/goals/add")
    public ResponseEntity<?> addGoal(@RequestBody GoalDTO request)
    {
        if (util.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");
        }

        log.info("Added new Goal");
        return ResponseEntity.ok(goalService.addGoal(request,util.getUser()));


    }
    @GetMapping("/goals/get")
    public ResponseEntity<?> getGoal()
    {
        if(util.getUser()==null)
        {
            if (util.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in");
            }
        }
        log.info("Getting Goal");
        return ResponseEntity.ok(goalService.getAllGoal());
    }

    @DeleteMapping("/goals/delete/{id}")
    public ResponseEntity<Void> deleteTransactions(@PathVariable Integer id) {
        goalService.deleteGoal(id);
        log.info("Deleted Successfully");
        return ResponseEntity.noContent().build();
    }

}
