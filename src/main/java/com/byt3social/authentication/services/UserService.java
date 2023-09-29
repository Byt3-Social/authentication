package com.byt3social.authentication.services;

import com.byt3social.authentication.models.JWTPayload;
import com.byt3social.authentication.models.User;
import com.byt3social.authentication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public User registerUser(JWTPayload jwtPayload) {
        User user = new User(jwtPayload);
        userRepository.save(user);

        rabbitTemplate.convertAndSend("autenticacao.ex", "", user);

        return user;
    }
    @Transactional
    public User updateUserLastLogin(User user) {
        user.updateLastLogin();

        return user;
    }
}
