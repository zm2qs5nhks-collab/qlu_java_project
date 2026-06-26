package com.demo.student_score.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "student", uniqueConstraints = {
        @UniqueConstraint(columnNames = "student_no") // 学号唯一约束
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_no", unique = true, nullable = false)
    private String studentNo; // 自动生成学号，前端不可输入、不可修改

    private String name;
    private String gender;
    private LocalDate birthday;

    // 多对一关联班级
    @ManyToOne
    @JoinColumn(name = "cls_id")
    private Cls cls;
}
