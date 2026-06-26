package com.demo.student_score.repository;

import com.demo.student_score.entity.Cls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClsRepository extends JpaRepository<Cls, Integer> {
}