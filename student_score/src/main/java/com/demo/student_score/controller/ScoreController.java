package com.demo.student_score.controller;

import com.demo.student_score.entity.Course;
import com.demo.student_score.entity.Score;
import com.demo.student_score.entity.Student;
import com.demo.student_score.service.CourseService;
import com.demo.student_score.service.ScoreReportService;
import com.demo.student_score.service.ScoreService;
import com.demo.student_score.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class ScoreController {
    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScoreReportService reportService;

    // ========== 成绩列表 + 搜索（学号精准 / 姓名模糊）==========
    @GetMapping("/score/list")
    public String scoreList(@RequestParam(required = false) String studentNo,
                            @RequestParam(required = false) String name,
                            Model model) {
        List<Score> scoreList;

        if (studentNo != null && !studentNo.isBlank()) {
            // 按学号精准查询
            scoreList = scoreService.getByStudentNo(studentNo);
            if (scoreList.isEmpty()) {
                model.addAttribute("msg", "未找到学号为 " + studentNo + " 的学生成绩");
            }
        } else if (name != null && !name.isBlank()) {
            // 按姓名模糊查询
            scoreList = scoreService.getByStudentNameLike(name);
            if (scoreList.isEmpty()) {
                model.addAttribute("msg", "未找到姓名为 " + name + " 的学生成绩");
            }
        } else {
            scoreList = scoreService.getAll();
        }

        model.addAttribute("scoreList", scoreList);
        model.addAttribute("studentNo", studentNo);
        model.addAttribute("name", name);
        return "scoreList";
    }

    // ========== 批量录入：选课→展示所有学生→填分 ==========
    @GetMapping("/score/toAddBatch")
    public String toAddBatch(@RequestParam(required = false) Integer courseId, Model model) {
        List<Course> courseList = courseService.getAll();
        model.addAttribute("courseList", courseList);

        if (courseId != null) {
            model.addAttribute("selectedCourseId", courseId);
            Optional<Course> course = courseService.getById(courseId);
            course.ifPresent(c -> model.addAttribute("selectedCourseName", c.getName()));

            // 展示所有学生
            List<Student> studentList = studentService.getAll();
            model.addAttribute("studentList", studentList);

            // 查询已有成绩用于回显
            Map<Integer, Float> existingScoreMap = new HashMap<>();
            for (Student stu : studentList) {
                Optional<Score> existScore = scoreService.getByStudentIdAndCourseId(stu.getId(), courseId);
                existScore.ifPresent(s -> existingScoreMap.put(stu.getId(), s.getScore()));
            }
            model.addAttribute("existingScoreMap", existingScoreMap);
        }

        return "addScoreBatch";
    }

    // 批量保存成绩
    @PostMapping("/score/saveBatch")
    public String saveBatch(@RequestParam Integer courseId,
                            @RequestParam List<Integer> studentIds,
                            @RequestParam List<Float> scores,
                            Model model) {
        try {
            scoreService.saveBatch(courseId, studentIds, scores);
            model.addAttribute("msg", "成绩批量保存成功！");
        } catch (Exception e) {
            model.addAttribute("err", "保存失败：" + e.getMessage());
        }
        // 保存后回到当前课程页面
        return toAddBatch(courseId, model);
    }

    // ========== 单条成绩编辑（保留兼容）==========
    @GetMapping("/score/toAdd")
    public String toAdd(Model model) {
        model.addAttribute("score", new Score());
        model.addAttribute("studentList", studentService.getAll());
        model.addAttribute("courseList", courseService.getAll());
        return "addScore";
    }

    @GetMapping("/score/toEdit/{id}")
    public String toEdit(@PathVariable Integer id, Model model) {
        Optional<Score> score = scoreService.getById(id);
        score.ifPresent(s -> model.addAttribute("score", s));
        model.addAttribute("studentList", studentService.getAll());
        model.addAttribute("courseList", courseService.getAll());
        return "addScore";
    }

    @PostMapping("/score/save")
    public String save(Score score) {
        scoreService.save(score);
        return "redirect:/score/list";
    }

    @GetMapping("/score/delete/{id}")
    public String delete(@PathVariable Integer id) {
        scoreService.delete(id);
        return "redirect:/score/list";
    }

    // ========== Excel导出 ==========
    @GetMapping("/score/export")
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<Score> scoreList = scoreService.getAll();
        scoreService.exportScoreExcel(scoreList, response);
    }

    // ========== 成绩分布图 ==========
    @GetMapping("/score/distribution")
    public String scoreDistribution(@RequestParam(required = false) Integer courseId, Model model) {
        Map<String, Long> dataMap = scoreService.getScoreDistribution(courseId);
        model.addAttribute("scoreData", dataMap);
        model.addAttribute("courseList", courseService.getAll());
        model.addAttribute("selectedCourseId", courseId);
        return "scoreDistribution";
    }

    // ========== 学习情况报表 ==========
    @GetMapping("/score/report")
    public String report(Model model) {
        List<ScoreReportService.ReportRow> rows = reportService.generateReport();
        model.addAttribute("reportRows", rows);
        return "scoreReport";
    }

    // 报表导出TXT
    @GetMapping("/score/report/exportTxt")
    public void exportReportTxt(HttpServletResponse response) throws Exception {
        List<ScoreReportService.ReportRow> rows = reportService.generateReport();
        response.setContentType("text/plain;charset=UTF-8");
        String fileName = URLEncoder.encode("成绩表.txt", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        OutputStream os = response.getOutputStream();
        reportService.exportReportTxt(rows, os);
        os.flush();
        os.close();
    }

    // 报表导出Excel（POI加分项）
    @GetMapping("/score/report/exportExcel")
    public void exportReportExcel(HttpServletResponse response) throws Exception {
        List<ScoreReportService.ReportRow> rows = reportService.generateReport();
        reportService.exportReportExcel(rows, response);
    }
}