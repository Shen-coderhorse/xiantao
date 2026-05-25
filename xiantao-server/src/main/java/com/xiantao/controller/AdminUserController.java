package com.xiantao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiantao.common.Result;
import com.xiantao.entity.User;
import com.xiantao.service.UserService;
import com.xiantao.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/list")
    public Result<List<UserVO>> getUserList(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);

        List<User> users = userService.list(wrapper);
        List<UserVO> voList = users.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("操作成功", null);
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam String role) {

        Long userId = (Long) request.getAttribute("userId");
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "无权限访问");
        }

        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setRole(role);
        userService.updateById(user);
        return Result.success("操作成功", null);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
