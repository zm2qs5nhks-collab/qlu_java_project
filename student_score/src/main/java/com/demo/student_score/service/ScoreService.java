package com.demo.student_score.service;

import com.demo.student_score.entity.Course;
import com.demo.student_score.entity.Score;
import com.demo.student_score.entity.Student;
import com.demo.student_score.repository.CourseRepository;
import com.demo.student_score.repository.ScoreRepository;
import com.demo.student_score.repository.StudentRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScoreService {
    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private CourseRepository courseRepo;

    // 全部成绩
    public List<Score> getAll() {
        return scoreRepo.findAll();
    }

    // 根据学生id查成绩
    public List<Score> getByStudentId(Integer studentId) {
        return scoreRepo.findByStudentId(studentId);
    }

    // 根据学号精准查询成绩
    public List<Score> getByStudentNo(String studentNo) {
        return scoreRepo.findByStudentNo(studentNo);
    }

    // 根据学生姓名模糊查询成绩
    public List<Score> getByStudentNameLike(String name) {
        return scoreRepo.findByStudentNameLike(name);
    }

    public Optional<Score> getById(Integer id) {
        return scoreRepo.findById(id);
    }

    public Score save(Score score) {
        return scoreRepo.save(score);
    }

    public void delete(Integer id) {
        scoreRepo.deleteById(id);
    }

    // 根据学生ID和课程ID查找已有成绩（用于批量录入时回显）
    public Optional<Score> getByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        return scoreRepo.findByStudentIdAndCourseId(studentId, courseId);
    }

    // 根据课程ID查询该课程所有成绩
    public List<Score> getByCourseId(Integer courseId) {
        return scoreRepo.findByCourseId(courseId);
    }

    // 批量保存成绩：已有则更新，没有则新增
    public void saveBatch(Integer courseId, List<Integer> studentIds, List<Float> scores) {
        for (int i = 0; i < studentIds.size(); i++) {
            Float scoreValue = scores.get(i);
            if (scoreValue == null) continue; // 跳过未填的

            Integer studentId = studentIds.get(i);
            Optional<Score> exist = scoreRepo.findByStudentIdAndCourseId(studentId, courseId);
            Score score;
            if (exist.isPresent()) {
                score = exist.get();
                score.setScore(scoreValue);
            } else {
                score = new Score();
                score.setScore(scoreValue);
                // 需要设置关联对象——通过Service层或直接设id
                // 用getReferenceById避免额外查询
                score.setStudent(studentRepo.getReferenceById(studentId));
                score.setCourse(courseRepo.getReferenceById(courseId));
            }
            scoreRepo.save(score);
        }
    }

    // Excel导出方法
    public void exportScoreExcel(List<Score> scoreList, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("成绩表");

        // 表头
        String[] headers = {"序号", "学生姓名", "课程名称", "分数"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充成绩数据
        int rowNum = 1;
        for (Score score : scoreList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(score.getStudent().getName());
            row.createCell(2).setCellValue(score.getCourse().getName());
            row.createCell(3).setCellValue(score.getScore());
        }

        // 设置浏览器下载
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("成绩数据.xlsx", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        workbook.close();
    }

    // 成绩分数段统计（支持按课程过滤，courseId为null则统计全部）
    public Map<String, Long> getScoreDistribution(Integer courseId) {
        List<Score> list;
        if (courseId != null) {
            list = scoreRepo.findByCourseId(courseId);
        } else {
            list = getAll();
        }
        Map<String, Long> scoreMap = new LinkedHashMap<>();
        scoreMap.put("0~60分", list.stream().filter(score -> score.getScore() >= 0 && score.getScore() < 60).count());
        scoreMap.put("60~70分", list.stream().filter(score -> score.getScore() >= 60 && score.getScore() < 70).count());
        scoreMap.put("70~80分", list.stream().filter(score -> score.getScore() >= 70 && score.getScore() < 80).count());
        scoreMap.put("80~90分", list.stream().filter(score -> score.getScore() >= 80 && score.getScore() < 90).count());
        scoreMap.put("90~100分", list.stream().filter(score -> score.getScore() >= 90 && score.getScore() <= 100).count());
        return scoreMap;
    }
}