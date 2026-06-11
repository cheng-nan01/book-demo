package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.service.AuthService;
import com.example.util.Result;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = authService.login(request.getUsername(), request.getPassword());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("createdAt", user.getCreatedAt().toString());
            session.setAttribute("user", userInfo);

            Map<String, Object> data = new HashMap<>();
            data.put("user", userInfo);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getUsername(), request.getPassword());
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setCreatedAt(user.getCreatedAt().toString());
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    public Result<String> changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("user");
            if (userInfo == null) {
                return Result.unauthorized("未登录");
            }
            Long userId = Long.valueOf(userInfo.get("id").toString());
            authService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/username")
    public Result<String> changeUsername(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("user");
            if (userInfo == null) {
                return Result.unauthorized("未登录");
            }
            Long userId = Long.valueOf(userInfo.get("id").toString());
            String newUsername = authService.changeUsername(userId, body.get("username"), body.get("password"));
            userInfo.put("username", newUsername);
            session.setAttribute("user", userInfo);
            return Result.success("用户名修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
