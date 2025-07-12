package com.example.finance.banking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSucccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException, ServletException, IOException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
        log.info("Get UserDetails: {}",(String) authentication.getPrincipal());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String json = String.format("{\"username\": \"%s\", \"roles\": %s}",
                user.getUsername(), user.getAuthorities().toString());

        response.getWriter().write(json);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Get UserDetails: {}",authentication.getPrincipal());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        log.info("The Username "+ username+" is logged in");
        String json = String.format("{\"username\": \"%s\", \"roles\": %s}",
                userDetails.getUsername(), userDetails.getAuthorities().toString());

        response.getWriter().write(json);
    }
}
