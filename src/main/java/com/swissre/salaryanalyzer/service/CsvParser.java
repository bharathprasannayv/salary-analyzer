package com.swissre.salaryanalyzer.service;

import com.swissre.salaryanalyzer.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class CsvParser {
    public Map<String, Employee> parse(MultipartFile file) {
        Map<String, Employee> employees = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String header = reader.readLine();
            if (header == null || header.trim().isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty or missing header");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, employees);
            }
            linkManagersAndSubordinates(employees);
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Error processing file: " + e.getMessage(), e);
        }
        return employees;
    }

    private void processLine(String line, Map<String, Employee> employees) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }
        String[] parts = line.split(",");
        if (parts.length < 4 || parts.length > 5) {
            throw new IllegalArgumentException("Line has invalid number of fields. Expected 4 or 5. Line: " + line);
        }
        String id = parts[0].trim();
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        String salaryStr = parts[3].trim();
        String managerId = parts.length == 4 ? null : parts[4].trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty. Line: " + line);
        }
        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary <= 0) {
                throw new IllegalArgumentException("Salary must be a positive number. Line: " + line);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid salary format. Line: " + line);
        }

        employees.put(id, new Employee(id, firstName, lastName, salary, managerId));
    }

    private void linkManagersAndSubordinates(Map<String, Employee> employees) {
        for (Employee employee : employees.values()) {
            if (employee.getManagerId() != null) {
                Employee manager = employees.get(employee.getManagerId());
                if (manager == null) {
                    throw new IllegalArgumentException("Manager not found for employee " + employee.getId());
                }
                manager.addDirectSubordinate(employee);
            }
        }
        long ceoCount = employees.values().stream()
                .filter(e -> e.getManagerId() == null)
                .count();

        if (ceoCount > 1) {
            throw new IllegalArgumentException("Multiple employees without managers found. Only one CEO is allowed.");
        }
    }
}
