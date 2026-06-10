package com.example.finance.banking.controller;

import com.example.finance.banking.service.ExcelReportService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/export")
public class ExcelReportController {
    private final UserService userService;
    private final UserDetailUtil util;
    private final ExcelReportService excelReportService;

    public ExcelReportController(UserService userService, UserDetailUtil util, ExcelReportService excelReportService) {
        this.userService = userService;
        this.util = util;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/dummy-report")
    public ResponseEntity<byte[]> downloadDummyReport() {
        try {
            if (!util.isValidUser()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authorized in".getBytes());
            }
            byte[] excelFile = excelReportService.generateDummyFinanceReport(util.getUser().getId());
            String month = LocalDate.now().getMonth().toString();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=finance-report-"+month+".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
