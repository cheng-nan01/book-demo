package com.example.service;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Transactional
    public User register(String username, String password) {
        if (userMapper.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.prePersist();
        userMapper.insert(user);
        return user;
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }
        userMapper.updatePassword(userId, newPassword);
    }

    @Transactional
    public String changeUsername(Long userId, String newUsername, String password) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (newUsername.equals(user.getUsername())) {
            throw new RuntimeException("新用户名不能与当前用户名相同");
        }
        if (userMapper.existsByUsernameExcludeId(newUsername, userId)) {
            throw new RuntimeException("该用户名已被占用");
        }
        userMapper.updateUsername(userId, newUsername);
        return newUsername;
    }
}
