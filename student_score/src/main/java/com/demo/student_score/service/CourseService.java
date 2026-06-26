package com.demo.student_score.service;

import com.demo.student_score.entity.Course;
import com.demo.student_score.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    public List<Course> getAll() {
        return courseRepo.findAll();
    }

    public Optional<Course> getById(Integer id) {
        return courseRepo.findById(id);
    }

    public Course save(Course course) {
        return courseRepo.save(course);
    }

    public void delete(Integer id) {
        courseRepo.deleteById(id);
    }
}