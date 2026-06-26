package com.demo.student_score.repository;

import com.demo.student_score.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    // 根据用户名查询账号（登录用）
    Optional<UserAccount> findByUsername(String username);
}