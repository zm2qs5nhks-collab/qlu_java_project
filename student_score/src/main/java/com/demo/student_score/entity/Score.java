package com.demo.student_score.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Float score;

    // 多对一关联学生，外键自动用student_id
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // 多对一关联课程，外键自动用course_id
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}