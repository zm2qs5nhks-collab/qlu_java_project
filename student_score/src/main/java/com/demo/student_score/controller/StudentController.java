package com.demo.student_score.controller;

import com.demo.student_score.entity.Student;
import com.demo.student_score.service.ClsService;
import com.demo.student_score.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClsService clsService;

    // 学生列表 + 模糊查询
    @GetMapping("/student/list")
    public String studentList(@RequestParam(required = false) String name, Model model){
        List<Student> studentList;
        if(name == null || "".equals(name)){
            studentList = studentService.getAll();
        }else{
            studentList = studentService.searchByName(name);
        }
        model.addAttribute("studentList", studentList);
        model.addAttribute("name", name);
        return "studentList";
    }

    @GetMapping("/student/toAdd")
    public String toAdd(Model model){
        // 新增页面传入空学生对象，避免前端null取值报错
        model.addAttribute("student", new Student());
        // 查询所有班级，传给前端下拉框
        model.addAttribute("clsList", clsService.getAll());
        return "addStudent";
    }

    @PostMapping("/student/save")
    public String save(Student student){
        studentService.save(student);
        return "redirect:/student/list";
    }

    @GetMapping("/student/delete/{id}")
    public String delete(@PathVariable Integer id){
        studentService.delete(id);
        return "redirect:/student/list";
    }

    @GetMapping("/student/toEdit/{id}")
    public String toEdit(@PathVariable Integer id, Model model){
        Optional<Student> student = studentService.getById(id);
        student.ifPresent(s -> model.addAttribute("student", s));
        // 编辑页同步传入班级数据
        model.addAttribute("clsList", clsService.getAll());
        return "addStudent";
    }
}