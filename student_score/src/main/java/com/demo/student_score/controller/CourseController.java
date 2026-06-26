package com.demo.student_score.controller;

import com.demo.student_score.entity.Course;
import com.demo.student_score.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/course/list")
    public String courseList(Model model){
        List<Course> courseList = courseService.getAll();
        model.addAttribute("courseList", courseList);
        return "courseList";
    }

    @GetMapping("/course/toAdd")
    public String toAdd(Model model){
        // 新增页面必须存入空Course对象，避免前端null取值报错
        model.addAttribute("course", new Course());
        return "addCourse";
    }

    @PostMapping("/course/save")
    public String save(Course course){
        courseService.save(course);
        return "redirect:/course/list";
    }

    @GetMapping("/course/delete/{id}")
    public String delete(@PathVariable Integer id){
        courseService.delete(id);
        return "redirect:/course/list";
    }

    @GetMapping("/course/toEdit/{id}")
    public String toEdit(@PathVariable Integer id, Model model){
        Optional<Course> course = courseService.getById(id);
        course.ifPresent(c -> model.addAttribute("course", c));
        return "addCourse";
    }
}