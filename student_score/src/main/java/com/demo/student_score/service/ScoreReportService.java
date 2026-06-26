package com.demo.student_score.service;

import com.demo.student_score.entity.Course;
import com.demo.student_score.entity.Score;
import com.demo.student_score.entity.Student;
import com.demo.student_score.repository.CourseRepository;
import com.demo.student_score.repository.ScoreRepository;
import com.demo.student_score.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;

@Service
public class ScoreReportService {
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private ScoreRepository scoreRepo;
    @Autowired
    private CourseRepository courseRepo;

    /**
     * 报表行数据结构
     */
    public static class ReportRow {
        public String studentNo;
        public String studentName;
        public String className;
        public Map<String, Float> courseScores = new LinkedHashMap<>(); // 课程名→成绩
        public Map<String, Float> courseClassAvgs = new LinkedHashMap<>(); // 课程名→班级均值
        public float totalScore;
        public float classTotalAvg;

        public float getTotalScore() { return totalScore; }
    }

    /**
     * 生成学习情况报表：每个学生一行，含各科成绩、班级均值、总分，
     * 按总分降序排列
     */
    public List<ReportRow> generateReport() {
        List<Student> students = studentRepo.findAll();
        List<Course> courses = courseRepo.findAll();
        List<Score> allScores = scoreRepo.findAll();

        // 1. 计算每门课程的全校平均分（作为"班级均值"）
        Map<Integer, Float> courseAvgMap = new HashMap<>();
        for (Course course : courses) {
            List<Score> courseScores = allScores.stream()
                    .filter(s -> s.getCourse().getId().equals(course.getId()))
                    .toList();
            double avg = courseScores.stream()
                    .mapToDouble(Score::getScore)
                    .average()
                    .orElse(0);
            courseAvgMap.put(course.getId(), (float) Math.round(avg * 100) / 100);
        }

        // 2. 计算全校总平均分
        double grandTotalAvg = allScores.stream()
                .mapToDouble(Score::getScore)
                .average()
                .orElse(0);
        float classTotalAvg = (float) Math.round(grandTotalAvg * 100) / 100;

        // 3. 为每个学生构造一行
        List<ReportRow> rows = new ArrayList<>();
        for (Student stu : students) {
            ReportRow row = new ReportRow();
            row.studentNo = stu.getStudentNo();
            row.studentName = stu.getName();
            row.className = stu.getCls() != null ? stu.getCls().getName() : "";

            float total = 0;
            int courseCount = 0;
            for (Course course : courses) {
                Optional<Score> scoreOpt = allScores.stream()
                        .filter(s -> s.getStudent().getId().equals(stu.getId())
                                  && s.getCourse().getId().equals(course.getId()))
                        .findFirst();
                float sc = scoreOpt.map(Score::getScore).orElse(0f);
                row.courseScores.put(course.getName(), sc);
                row.courseClassAvgs.put(course.getName(), courseAvgMap.getOrDefault(course.getId(), 0f));
                total += sc;
                if (scoreOpt.isPresent()) courseCount++;
            }
            row.totalScore = (float) Math.round(total * 100) / 100;
            row.classTotalAvg = classTotalAvg;
            rows.add(row);
        }

        // 4. 按总成绩降序排列
        rows.sort((a, b) -> Float.compare(b.totalScore, a.totalScore));

        return rows;
    }

    /**
     * 导出报表为 TXT 文件
     */
    public void exportReportTxt(List<ReportRow> rows, OutputStream os) throws Exception {
        PrintWriter writer = new PrintWriter(os, true);
        writer.println("==============================================");
        writer.println("           学 生 学 习 情 况 报 表");
        writer.println("==============================================");
        writer.println();

        // 表头
        StringBuilder header = new StringBuilder();
        header.append(String.format("%-12s", "学号"));
        header.append(String.format("%-8s", "姓名"));
        if (!rows.isEmpty() && !rows.get(0).courseScores.isEmpty()) {
            for (String courseName : rows.get(0).courseScores.keySet()) {
                header.append(String.format("%-8s", courseName));
                header.append(String.format("%-10s", courseName + "均值"));
            }
        }
        header.append(String.format("%-8s", "总分"));
        header.append(String.format("%-10s", "总平均"));
        writer.println(header.toString());
        writer.println(String.join("", Collections.nCopies(header.length(), "-")));

        // 数据行
        for (int i = 0; i < rows.size(); i++) {
            ReportRow row = rows.get(i);
            StringBuilder line = new StringBuilder();
            line.append(String.format("%-12s", row.studentNo));
            line.append(String.format("%-8s", row.studentName));
            for (Map.Entry<String, Float> entry : row.courseScores.entrySet()) {
                line.append(String.format("%-8s", entry.getValue()));
                line.append(String.format("%-10s", row.courseClassAvgs.get(entry.getKey())));
            }
            line.append(String.format("%-8s", row.totalScore));
            line.append(String.format("%-10s", row.classTotalAvg));
            writer.println(line.toString());
        }

        writer.println();
        writer.println("共 " + rows.size() + " 名学生");
        writer.flush();
    }

    /**
     * 导出报表为 Excel 文件（POI加分项）
     */
    public void exportReportExcel(List<ReportRow> rows, jakarta.servlet.http.HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学习情况报表");

        // 样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 表头
        int col = 0;
        Row headerRow = sheet.createRow(0);
        String[] baseHeaders = {"排名", "学号", "姓名", "班级"};
        for (String h : baseHeaders) {
            Cell cell = headerRow.createCell(col++);
            cell.setCellValue(h);
            cell.setCellStyle(headerStyle);
        }
        // 动态课程列
        List<String> courseNames = new ArrayList<>();
        if (!rows.isEmpty()) {
            courseNames.addAll(rows.get(0).courseScores.keySet());
        }
        for (String cn : courseNames) {
            Cell cell = headerRow.createCell(col++);
            cell.setCellValue(cn + "成绩");
            cell.setCellStyle(headerStyle);
            cell = headerRow.createCell(col++);
            cell.setCellValue(cn + "均值");
            cell.setCellStyle(headerStyle);
        }
        Cell totalCell = headerRow.createCell(col++);
        totalCell.setCellValue("总分");
        totalCell.setCellStyle(headerStyle);
        Cell avgCell = headerRow.createCell(col++);
        avgCell.setCellValue("总平均分");
        avgCell.setCellStyle(headerStyle);

        // 数据行
        int rowNum = 1;
        for (ReportRow row : rows) {
            Row dataRow = sheet.createRow(rowNum++);
            int c = 0;
            dataRow.createCell(c++).setCellValue(rowNum - 1);  // 排名
            dataRow.createCell(c++).setCellValue(row.studentNo);
            dataRow.createCell(c++).setCellValue(row.studentName);
            dataRow.createCell(c++).setCellValue(row.className);
            for (String cn : courseNames) {
                dataRow.createCell(c++).setCellValue(row.courseScores.getOrDefault(cn, 0f));
                dataRow.createCell(c++).setCellValue(row.courseClassAvgs.getOrDefault(cn, 0f));
            }
            dataRow.createCell(c++).setCellValue(row.totalScore);
            dataRow.createCell(c++).setCellValue(row.classTotalAvg);
        }

        // 自动列宽
        for (int i = 0; i < col; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("学习情况报表.xlsx", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        workbook.close();
    }
}
