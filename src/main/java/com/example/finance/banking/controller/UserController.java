package com.example.finance.banking.controller;

import com.example.finance.banking.dto.LoginRequestDTO;
import com.example.finance.banking.dto.UserRequestDTO;
import com.example.finance.banking.mapper.Mapper;
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

@Slf4j
@RestController
public class UserController {

    private final UserService userService;
    private final Mapper mapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailUtil util;

    @Autowired
    public UserController(UserService userService, Mapper mapper,
                          AuthenticationManager authenticationManager, UserDetailUtil util) {
        this.userService = userService;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.util = util;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody UserRequestDTO userRequestDTO) {
        if (userService.isEmailExist(userRequestDTO.getEmail())) {
            log.error("Email already exists: {}", userRequestDTO.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }
        userService.saveUser(mapper.mappingUsertoUserDTO(userRequestDTO));
        return ResponseEntity.ok(userRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok("Login successful");
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok(auth.getName());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
    }
}
