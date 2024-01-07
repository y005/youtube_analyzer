package com.example.project01.youtube.service;

import com.example.project01.youtube.dto.UserSearchParam;
import com.example.project01.youtube.entity.UserEntity;
import com.example.project01.youtube.model.User;
import com.example.project01.youtube.mapper.UserMapper;
import com.example.project01.youtube.repository.UserQdslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserMapper userMapper;
    private final UserQdslRepository userQdslRepository;

    public User findByUsername(String userId) {
        UserSearchParam userSearchParam = UserSearchParam.of(userId, null);
        Pageable pageable = PageRequest.of(0, 1);
        Optional<UserEntity> userEntity = userQdslRepository.findByParam(userSearchParam, pageable)
                .stream()
                .findAny();
        return userEntity.map(User::of).orElse(null);
    }

    @Transactional
    public void save(User user) {
        userMapper.save(user);
    }

    public User login(String userId, String userPassword) {
        User user = findByUsername(userId);

        if (user == null || !userPassword.equals(user.getPassword())) {
            throw new RuntimeException("로그인 정보에 맞는 계정이 존재하지 않습니다.");
        }
        return user;
    }

    public List<User> getEveryUser(int page, int size) {
        UserSearchParam userSearchParam = UserSearchParam.of(null, null);
        Pageable pageable = PageRequest.of(page, size);
        return userQdslRepository.findByParam(userSearchParam, pageable)
                .stream()
                .map(User::of)
                .collect(Collectors.toList());
    }
}
