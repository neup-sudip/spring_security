package com.example.security.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class CustomPasswordEncoder {
    public String encode(CharSequence rawPassword) {
        String passwordString = rawPassword.toString();
        return BCrypt.hashpw(passwordString, BCrypt.gensalt(10));
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String passwordString = rawPassword.toString();
        passwordString = passwordString.substring(3);
        return BCrypt.checkpw(passwordString, encodedPassword);
    }
}
