package com.demo.student_score.service;

import com.demo.student_score.entity.Cls;
import com.demo.student_score.repository.ClsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClsService {
    @Autowired
    private ClsRepository clsRepo;

    public List<Cls> getAll() {
        return clsRepo.findAll();
    }

    public Optional<Cls> getById(Integer id) {
        return clsRepo.findById(id);
    }

    public Cls save(Cls cls) {
        return clsRepo.save(cls);
    }

    public void delete(Integer id) {
        clsRepo.deleteById(id);
    }
}