package com.demo.student_score.service;

import com.demo.student_score.entity.Course;
import com.demo.student_score.entity.Score;
import com.demo.student_score.entity.Student;
import com.demo.student_score.repository.CourseRepository;
import com.demo.student_score.repository.ScoreRepository;
import com.demo.student_score.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

@Service
public class DataGeneratorService {
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private ScoreRepository scoreRepo;

    // 姓和名素材库
    private static final String[] SURNAMES = {
        "张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴",
        "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗"
    };
    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "洋",
        "勇", "艳", "杰", "涛", "明", "超", "秀兰", "霞", "平", "刚",
        "文", "华", "飞", "玉兰", "桂花", "波", "斌", "军", "辉", "玲",
        "建国", "建华", "建军", "志强", "志明", "国强", "国栋", "雪梅", "海燕", "秀珍"
    };

    private final Random random = new Random();

    /**
     * 生成测试数据
     * @param count 学生数量
     * @param mean 成绩均值
     * @param stdDev 成绩标准差
     * @param filePath 输出txt文件路径（可选）
     * @param insertToDb 是否同时插入数据库
     * @return 生成结果统计
     */
    @Transactional
    public Map<String, Object> generateTestData(int count, double mean, double stdDev,
                                                  String filePath, boolean insertToDb) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        long startTime = System.currentTimeMillis();

        // 1. 确保三门课程存在（数学、Java、体育）
        List<Course> courses = ensureCoursesExist();

        // 2. 获取当前最大学号
        String maxNo = studentRepo.findMaxStudentNo();
        long startNum = 20260001L;
        if (maxNo != null) {
            startNum = Long.parseLong(maxNo) + 1;
        }

        // 3. 生成学生和成绩，写入txt文件
        PrintWriter writer = null;
        if (filePath != null && !filePath.isBlank()) {
            writer = new PrintWriter(filePath, "UTF-8");
            writer.println("学号,姓名,性别,出生日期," + courses.get(0).getName() + "," + courses.get(1).getName() + "," + courses.get(2).getName());
        }

        // 批量插入缓冲
        List<Student> studentBatch = new ArrayList<>(1000);
        List<Score> scoreBatch = new ArrayList<>(3000);

        int totalInserted = 0;
        for (int i = 0; i < count; i++) {
            // 生成学生
            Student student = new Student();
            String studentNo = String.valueOf(startNum + i);
            student.setStudentNo(studentNo);
            student.setName(generateName());
            student.setGender(random.nextBoolean() ? "男" : "女");
            int year = 1995 + random.nextInt(16);
            int month = 1 + random.nextInt(12);
            int day = 1 + random.nextInt(28);
            student.setBirthday(LocalDate.of(year, month, day));

            // 生成三科成绩（正态分布N(mean, stdDev²)），统一用float
            float mathScore = (float) Math.round(clampScore(mean + random.nextGaussian() * stdDev) * 10) / 10;
            float javaScore = (float) Math.round(clampScore(mean + random.nextGaussian() * stdDev) * 10) / 10;
            float peScore = (float) Math.round(clampScore(mean + random.nextGaussian() * stdDev) * 10) / 10;
            float[] courseScores = {mathScore, javaScore, peScore};

            // 写入文件（每1万条打印一次进度）
            if (writer != null) {
                writer.printf("%s,%s,%s,%s,%.1f,%.1f,%.1f%n",
                        studentNo, student.getName(), student.getGender(),
                        student.getBirthday(), mathScore, javaScore, peScore);
                if ((i + 1) % 10000 == 0) {
                    writer.flush();
                    System.out.println("文件已生成 " + (i + 1) + " / " + count + " 条");
                }
            }

            // 插入数据库
            if (insertToDb) {
                studentBatch.add(student);
                if (studentBatch.size() >= 1000) {
                    studentRepo.saveAll(studentBatch); // 先保存学生获得ID
                    // 为这批学生创建成绩（用同样的分数）
                    for (Student s : studentBatch) {
                        for (int c = 0; c < courses.size(); c++) {
                            Score score = new Score();
                            score.setStudent(s);
                            score.setCourse(courses.get(c));
                            score.setScore((float) Math.round(
                                    clampScore(mean + random.nextGaussian() * stdDev) * 10) / 10);
                            scoreBatch.add(score);
                        }
                    }
                    scoreRepo.saveAll(scoreBatch);
                    totalInserted += studentBatch.size();
                    studentBatch.clear();
                    scoreBatch.clear();
                    System.out.println("数据库已插入 " + totalInserted + " / " + count + " 条");
                }
            }
        }

        // 处理剩余批次（DB模式）
        if (insertToDb && !studentBatch.isEmpty()) {
            studentRepo.saveAll(studentBatch);
            for (Student s : studentBatch) {
                for (int c = 0; c < courses.size(); c++) {
                    Score score = new Score();
                    score.setStudent(s);
                    score.setCourse(courses.get(c));
                    score.setScore((float) Math.round(
                            clampScore(mean + random.nextGaussian() * stdDev) * 10) / 10);
                    scoreBatch.add(score);
                }
            }
            scoreRepo.saveAll(scoreBatch);
            totalInserted += studentBatch.size();
        }

        // 仅生成文件模式
        if (!insertToDb) {
            totalInserted = count;
        }

        if (writer != null) {
            writer.close();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        result.put("status", "success");
        result.put("count", totalInserted);
        result.put("elapsedSeconds", elapsed / 1000.0);
        result.put("filePath", filePath);
        result.put("insertedToDb", insertToDb);
        result.put("mean", mean);
        result.put("stdDev", stdDev);

        return result;
    }

    // 确保三门课程存在
    private List<Course> ensureCoursesExist() {
        List<Course> all = courseRepo.findAll();
        Map<String, Course> map = new LinkedHashMap<>();
        for (Course c : all) {
            map.put(c.getName(), c);
        }
        String[] required = {"数学", "Java", "体育"};
        for (String name : required) {
            if (!map.containsKey(name)) {
                Course c = new Course();
                c.setName(name);
                c.setCredit(4); // 默认4学分
                courseRepo.save(c);
                map.put(name, c);
            }
        }
        return List.of(map.get("数学"), map.get("Java"), map.get("体育"));
    }

    // 随机生成姓名
    private String generateName() {
        String surname = SURNAMES[random.nextInt(SURNAMES.length)];
        String given = GIVEN_NAMES[random.nextInt(GIVEN_NAMES.length)];
        return surname + given;
    }

    // 限制成绩范围 0~100
    private double clampScore(double score) {
        return Math.max(0, Math.min(100, score));
    }
}
