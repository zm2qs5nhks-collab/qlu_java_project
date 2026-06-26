package com.demo.student_score.service;

import com.demo.student_score.entity.UserAccount;
import com.demo.student_score.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userRepo;

    // 根据用户名查询，用于登录校验
    public Optional<UserAccount> findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}