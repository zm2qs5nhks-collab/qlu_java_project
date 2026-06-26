package com.demo.student_score.service;

import com.demo.student_score.entity.Student;
import com.demo.student_score.repository.ScoreRepository;
import com.demo.student_score.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private ScoreRepository scoreRepo;

    // 查询全部学生
    public List<Student> getAll() {
        return studentRepo.findAll();
    }

    // 姓名模糊搜索
    public List<Student> searchByName(String name) {
        return studentRepo.findByNameContaining(name);
    }

    // 根据id查单个学生
    public Optional<Student> getById(Integer id) {
        return studentRepo.findById(id);
    }

    // 根据学号精准查询学生
    public Student getByStudentNo(String studentNo) {
        return studentRepo.findByStudentNo(studentNo);
    }

    // 自动生成学号，格式：20260001、20260002……
    public String generateStudentNo() {
        String maxNo = studentRepo.findMaxStudentNo();
        if (maxNo == null) {
            // 数据库暂无学生，从20260001开始
            return "20260001";
        }
        // 截取后4位数字自增
        int num = Integer.parseInt(maxNo.substring(4)) + 1;
        return "2026" + String.format("%04d", num);
    }

    // 新增/修改学生：新增自动生成学号，编辑强制沿用原有学号，禁止修改
    public Student save(Student student) {
        // 新增操作：id为空，自动生成唯一学号
        if (student.getId() == null) {
            student.setStudentNo(generateStudentNo());
        } else {
            // 编辑操作：从数据库读取原始学号，强制覆盖前端传入的学号，防止被篡改
            Student oldStudent = studentRepo.findById(student.getId()).get();
            student.setStudentNo(oldStudent.getStudentNo());
        }
        return studentRepo.save(student);
    }

    // 删除学生（先级联删除其所有成绩，再删学生）
    @Transactional
    public void delete(Integer id) {
        scoreRepo.deleteByStudentId(id);
        studentRepo.deleteById(id);
    }
}