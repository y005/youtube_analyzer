package com.example.project01.youtube.service;

import com.example.project01.youtube.entity.User;
import com.example.project01.youtube.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public User findByUsername(String userId) {
        return userRepository.findByUserId(userId);
    }


    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public User login(String userId, String userPassword) {
        return userRepository.findByUserIdWithPassword(userId, userPassword);
    }

    public List<User> getEveryUser(int offset, int limit) {
        return userRepository.getEveryUser(offset, limit);
    }
}
