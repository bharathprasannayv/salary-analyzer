package com.swissre.salaryanalyzer.service;

import com.swissre.salaryanalyzer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class SalaryAnalyzerServiceImpl implements SalaryAnalyzerService {

    private final CsvParser csvParser;

    @Autowired
    public SalaryAnalyzerServiceImpl(CsvParser csvParser){
        this.csvParser = csvParser;
    }

    public AnalysisResult analyze(MultipartFile file) throws IOException {
        Map<String, Employee> employees = csvParser.parse(file);
        AnalysisResult result = new AnalysisResult();
        for (Employee manager : employees.values()) {
            List<Employee> subordinates = manager.getDirectSubordinates();
            if (!subordinates.isEmpty()) {
                double averageSalary = subordinates.stream()
                        .mapToDouble(Employee::getSalary)
                        .average()
                        .orElse(0.0);
                double lowerBound = averageSalary * 1.2;
                double upperBound = averageSalary * 1.5;
                double managerSalary = manager.getSalary();

                if (managerSalary < lowerBound) {
                    result.getUnderpaidManagers().add(new UnderpaidManagerReport(manager, lowerBound - managerSalary));
                } else if (managerSalary > upperBound) {
                    result.getOverpaidManagers().add(new OverpaidManagerReport(manager, managerSalary - upperBound));
                }
            }
        }

        for (Employee employee : employees.values()) {
            int reportingLineLength = getReportingLineLength(employee.getId(), employees);
            if (reportingLineLength > 4) {
                result.getLongReportingLineEmployees().add(new LongReportingLineEmployee(employee, reportingLineLength - 4));
            }
        }
        return result;
    }

    public static int getReportingLineLength(String employeeId, Map<String, Employee> employees) {
        int levels = 0;
        Employee current = employees.get(employeeId);
        while (current != null && current.getManagerId() != null) {
            current = employees.get(current.getManagerId());
            levels++;
        }
        return levels;
    }
}
