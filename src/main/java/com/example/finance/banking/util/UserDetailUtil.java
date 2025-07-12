package com.example.finance.banking.util;

import com.example.finance.banking.entity.User;
import com.example.finance.banking.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDetailUtil {
    private final UserService userService;

    public UserDetailUtil(UserService userService) {
        this.userService = userService;
    }

    public String getUserName()
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public User getUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return userService.getUser(authentication.getName());
        }
        return null;
    }

}
