package com.xiantao.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiantao.common.Result;
import com.xiantao.entity.User;
import com.xiantao.service.UserService;
import com.xiantao.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!StringUtils.hasText(token) || !jwtUtils.validateToken(token)) {
            writeError(response, 401, "未登录或登录已过期");
            return false;
        }

        Long userId = jwtUtils.getUserId(token);
        if (!userService.isAdmin(userId)) {
            writeError(response, 403, "无权限访问，需要管理员身份");
            return false;
        }

        request.setAttribute("userId", userId);
        request.setAttribute("username", jwtUtils.getUsername(token));
        return true;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
}
