package com.demo.student_score.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(              // 放行登录相关
                        "/",
                        "/login",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}
