package com.demo.student_score.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        // 未登录则重定向到登录页
        if (session.getAttribute("user") == null) {
            response.sendRedirect("/");
            return false;
        }
        return true;
    }
}
