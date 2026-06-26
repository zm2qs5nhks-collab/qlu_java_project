package com.demo.student_score.controller;

import com.demo.student_score.entity.UserAccount;
import com.demo.student_score.service.UserAccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserAccountService userService;

    // 跳转登录页
    @GetMapping("/")
    public String toLogin(){
        return "login";
    }

    // 登录提交
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model, HttpSession session){
        Optional<UserAccount> user = userService.findUserByUsername(username);
        if(user.isPresent() && user.get().getPassword().equals(password)){
            session.setAttribute("user", user.get());
            return "redirect:/index";
        }
        model.addAttribute("msg","账号或密码错误");
        return "login";
    }

    // 首页（登录后）
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}