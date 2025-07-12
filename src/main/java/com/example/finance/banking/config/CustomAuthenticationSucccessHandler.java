package com.example.finance.banking.config;

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
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String roles = userDetails.getAuthorities().toString();

        log.info("User '{}' successfully logged in with roles {}", username, roles);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        String json = String.format("{\"username\": \"%s\", \"roles\": %s}",
                username, roles);

        response.getWriter().write(json);
    }
}
