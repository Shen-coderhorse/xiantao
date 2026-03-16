package com.xiantao.controller;

import com.xiantao.common.Result;
import com.xiantao.dto.LoginDTO;
import com.xiantao.dto.RegisterDTO;
import com.xiantao.dto.UserUpdateDTO;
import com.xiantao.service.UserService;
import com.xiantao.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        UserVO vo = userService.register(dto);
        return Result.success("注册成功", vo);
    }

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO dto) {
        UserVO vo = userService.login(dto);
        return Result.success("登录成功", vo);
    }
}

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    @PutMapping("/info")
    public Result<UserVO> updateUserInfo(HttpServletRequest request, @Valid @RequestBody UserUpdateDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.updateUserInfo(userId, dto);
        return Result.success("修改成功", vo);
    }
}
