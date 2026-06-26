package com.demo.student_score.repository;

import com.demo.student_score.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Integer> {
    List<Score> findByStudentId(Integer studentId);

    // 根据学生学号查询所有成绩
    @Query("SELECT s FROM Score s WHERE s.student.studentNo = :studentNo")
    List<Score> findByStudentNo(@Param("studentNo") String studentNo);

    // 根据学生姓名模糊查成绩
    @Query("SELECT s FROM Score s WHERE s.student.name LIKE %:name%")
    List<Score> findByStudentNameLike(@Param("name") String name);

    // 根据课程ID查询该课程所有成绩
    List<Score> findByCourseId(Integer courseId);

    // 根据学生ID和课程ID查找唯一成绩记录
    Optional<Score> findByStudentIdAndCourseId(Integer studentId, Integer courseId);

    // 级联删除：删除某学生的所有成绩
    @Modifying
    @Transactional
    @Query("DELETE FROM Score s WHERE s.student.id = :studentId")
    void deleteByStudentId(@Param("studentId") Integer studentId);
}
