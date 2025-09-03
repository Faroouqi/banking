package com.example.finance.banking.controller;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.example.finance.banking.dto.LoginRequestDTO;
import com.example.finance.banking.dto.UserRequestDTO;
import com.example.finance.banking.entity.User;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    @Autowired
    public UserController(UserService userService, Mapper mapper, UserDetailUtil util, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.mapper = mapper;
        this.util = util;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
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
        if(otpStore.isEmpty()){

            otpStore.put(email, getSixDigitRandomNumber());
        }

        emailService.sendVerificationEmail(email, otpStore.get(email));

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
            otpStore.remove(email);
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
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            User user = userService.getUser(username);
            log.info(user.getUsername());
            return ResponseEntity.ok(user.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
    }
    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> data) {
        String usernameOrEmail = data.get("username"); // Or "email"
        String newPassword = data.get("newPassword");
        log.info("resetpassword",usernameOrEmail);
        User user = userService.getUser(usernameOrEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // Validate user identity (optional: use security question, OTP, etc.)
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("while saving user",usernameOrEmail);// Use BCrypt
        userService.saveUser(user);
        return ResponseEntity.ok("Password reset successful.");
    }
}
