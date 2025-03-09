package com.swissre.salaryanalyzer.controller;

import com.swissre.salaryanalyzer.model.AnalysisResult;
import com.swissre.salaryanalyzer.service.SalaryAnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class SalaryAnalyzerController {
    private final SalaryAnalyzerService salaryAnalyzerService;

    @Autowired
    public SalaryAnalyzerController(SalaryAnalyzerService salaryAnalyzerService){
        this.salaryAnalyzerService = salaryAnalyzerService;

    }

    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        try {
            return salaryAnalyzerService.analyze(file);
        } catch (Exception e) {
            throw new RuntimeException("Error processing file", e);
        }
    }

}
