package com.demo.student_score.controller;

import com.demo.student_score.entity.Cls;
import com.demo.student_score.service.ClsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class ClsController {
    @Autowired
    private ClsService clsService;

    @GetMapping("/class/list")
    public String classList(Model model){
        List<Cls> classList = clsService.getAll();
        model.addAttribute("classList", classList);
        return "classList";
    }

    @GetMapping("/class/toAdd")
    public String toAdd(Model model){
        // 新增页面，传入空 Cls 对象，避免页面取值 null 报错
        model.addAttribute("cls", new com.demo.student_score.entity.Cls());
        return "addClass";
    }

    @PostMapping("/class/save")
    public String save(Cls cls){
        clsService.save(cls);
        return "redirect:/class/list";
    }

    @GetMapping("/class/delete/{id}")
    public String delete(@PathVariable Integer id){
        clsService.delete(id);
        return "redirect:/class/list";
    }

    @GetMapping("/class/toEdit/{id}")
    public String toEdit(@PathVariable Integer id, Model model){
        Optional<Cls> cls = clsService.getById(id);
        cls.ifPresent(c -> model.addAttribute("cls", c));
        return "addClass";
    }
}