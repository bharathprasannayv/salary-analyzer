package com.swissre.salaryanalyzer.service;

import com.swissre.salaryanalyzer.model.AnalysisResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SalaryAnalyzerService {
    AnalysisResult analyze(MultipartFile file) throws IOException;
}
