package com.example.finance.banking.controller;

import com.example.finance.banking.dto.LoginRequestDTO;
import com.example.finance.banking.dto.UserRequestDTO;
import com.example.finance.banking.mapper.Mapper;
import com.example.finance.banking.service.EmailService;
import com.example.finance.banking.service.UserService;
import com.example.finance.banking.util.UserDetailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    private final Mapper mapper;
    private final UserDetailUtil util;
    private final EmailService emailService;

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    @Autowired
    public UserController(UserService userService, Mapper mapper, UserDetailUtil util, EmailService emailService) {
        this.userService = userService;
        this.mapper = mapper;
        this.util = util;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody UserRequestDTO userRequestDTO) {
        log.debug("--Registration with userId: {}",userRequestDTO.getEmail());
        if (userService.isEmailExist(userRequestDTO.getEmail())) {
            log.error("Email already exists: {}", userRequestDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }
        userService.saveUser(mapper.mappingUsertoUserDTO(userRequestDTO));
        return ResponseEntity.ok(userRequestDTO);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email must be provided"));
        }

        String otp = getSixDigitRandomNumber();
        otpStore.put(email, otp);
        emailService.sendVerificationEmail(email, otp);

        return ResponseEntity.ok(Map.of(
                "email", email,
                "message", "OTP sent successfully"
        ));
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and OTP must be provided"));
        }

        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(email); // ✅ clear OTP once verified
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid OTP"));
        }
    }

    public static String getSixDigitRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(900000) + 100000;  // Generates a number between 100000 and 999999
        return String.valueOf(number);
    }
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser() {
        log.info("----Fetching user info--");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok(auth.getName());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
    }
}
