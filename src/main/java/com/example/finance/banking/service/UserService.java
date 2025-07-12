package com.example.finance.banking.service;

import com.example.finance.banking.entity.User;
import com.example.finance.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Could not find User"));
    }
    public void saveUser(User user)
    {
        userRepository.save(user);
    }

    public boolean isEmailExist(String email)
    {
        return userRepository.existsByEmail(email);
    }

}
