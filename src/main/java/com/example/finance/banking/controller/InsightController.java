package com.example.finance.banking.controller;

import com.example.finance.banking.dto.InsightDTO;
import com.example.finance.banking.service.InsightService;
import com.example.finance.banking.util.UserDetailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insights")
@Slf4j
public class InsightController {

    @Autowired
    InsightService insightService;
    @Autowired
    UserDetailUtil util;

    @GetMapping("/{month}")
    public ResponseEntity<List<InsightDTO>> getInsights(@PathVariable Integer month) {
        Integer userId = util.getUser().getId();
        log.info("Month is "+ month);
        return ResponseEntity.ok(insightService.generateInsights(userId,month));
    }
}
