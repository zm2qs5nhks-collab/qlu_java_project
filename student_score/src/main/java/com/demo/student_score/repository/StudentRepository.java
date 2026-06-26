package com.demo.student_score.repository;

import com.demo.student_score.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    // 根据姓名模糊查询学生
    List<Student> findByNameContaining(String name);

    // 根据学号精准查询
    Student findByStudentNo(String studentNo);

    // 查询最大学号，用于自动生成新学号
    @Query("SELECT MAX(s.studentNo) FROM Student s")
    String findMaxStudentNo();
}