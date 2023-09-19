package com.byt3social.authentication.services;

import com.byt3social.authentication.models.JWTPayload;
import com.byt3social.authentication.models.User;
import com.byt3social.authentication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User userExists(JWTPayload jwtPayload) {
        User user = userRepository.findByEmail(jwtPayload.getEmail().asString());

        return user;
    }

    public User registerUser(JWTPayload jwtPayload) {
        User user = new User(jwtPayload);
        userRepository.save(user);

        return user;
    }
    @Transactional
    public User updateUserLastLogin(User user) {
        user.updateLastLogin();

        return user;
    }
}
