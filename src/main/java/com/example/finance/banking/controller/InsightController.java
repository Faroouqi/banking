package com.example.finance.banking.controller;

import com.example.finance.banking.dto.InsightDTO;
import com.example.finance.banking.service.InsightService;
import com.example.finance.banking.util.UserDetailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/insights")
public class InsightController {

    @Autowired
    InsightService insightService;
    @Autowired
    UserDetailUtil util;

    @GetMapping("/")
    public ResponseEntity<List<InsightDTO>> getInsights() {
        Integer userId = util.getUser().getId();
        return ResponseEntity.ok(insightService.generateInsights(userId));
    }
}
