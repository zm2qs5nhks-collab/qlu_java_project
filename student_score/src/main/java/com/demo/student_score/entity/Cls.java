package com.demo.student_score.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "class")
public class Cls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}