package com.demo.student_score.controller;

import com.demo.student_score.service.DataGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class DataGeneratorController {
    @Autowired
    private DataGeneratorService dataGeneratorService;

    // 跳转生成工具页面
    @GetMapping("/test/generate")
    public String toGeneratePage() {
        return "dataGenerator";
    }

    // 执行数据生成
    @PostMapping("/test/generate")
    public String generate(@RequestParam(defaultValue = "100000") int count,
                           @RequestParam(defaultValue = "80") double mean,
                           @RequestParam(defaultValue = "10") double stdDev,
                           @RequestParam(defaultValue = "false") boolean insertToDb,
                           Model model) {
        try {
            // 生成文件路径（项目根目录）
            String filePath = System.getProperty("user.dir") + "/test_students_100k.txt";
            Map<String, Object> result = dataGeneratorService.generateTestData(
                    count, mean, stdDev, filePath, insertToDb);
            model.addAttribute("result", result);
            model.addAttribute("msg", "✅ 数据生成成功！");
        } catch (Exception e) {
            model.addAttribute("err", "❌ 生成失败：" + e.getMessage());
            e.printStackTrace();
        }
        return "dataGenerator";
    }
}
