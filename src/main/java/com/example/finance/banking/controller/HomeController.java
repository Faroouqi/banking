package com.example.finance.banking.controller;

import com.example.finance.banking.util.UserDetailUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final UserDetailUtil util;

    public HomeController(UserDetailUtil util) {
        this.util = util;
    }

    @GetMapping("/")
    public String get()
    {
        return "welcome";

    }
    @GetMapping("/authentication")
    public ResponseEntity<?> checkAuth() {
         if(util.getUser()!=null)
         {
             return ResponseEntity.ok("Authorized");
         }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized");
    }
}
