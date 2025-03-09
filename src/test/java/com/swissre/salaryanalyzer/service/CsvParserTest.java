package com.swissre.salaryanalyzer.service;

import com.swissre.salaryanalyzer.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CsvParserTest {
    @Autowired
    private CsvParser csvParserService;

    @Test
    void testParseValidCSV() throws Exception {
        // Valid CSV with one CEO and valid hierarchy
        String validCsv = "Id,firstName,lastName,salary,managerId\n" +
                "123,Joe,Doe,60000\n" +
                "124,Martin,Chekov,45000,123\n" +
                "125,Bob,Ronstad,47000,123";

        MockMultipartFile file = new MockMultipartFile("employees.csv", validCsv.getBytes());
        Map<String, Employee> employees = csvParserService.parse(file);

        assertEquals(3, employees.size());
        assertTrue(employees.containsKey("123"));
        assertTrue(employees.containsKey("124"));
        assertTrue(employees.containsKey("125"));
    }

    @Test
    void testparseWithInvalidFieldCount() {
        // Line with too few fields
        String invalidCsv = "Id,firstName,lastName,salary,managerId\n" +
                "123,Joe,Doe";

        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parse(file);
        });

        assertEquals("Line has invalid number of fields. Expected 4 or 5. Line: 123,Joe,Doe", exception.getMessage());
    }

    @Test
    void testparseWithInvalidSalary() {
        String invalidCsv = "Id,firstName,lastName,salary\n" +
                "123,Joe,Doe,invalid";
        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parse(file);
        });
        assertEquals("Invalid salary format. Line: 123,Joe,Doe,invalid", exception.getMessage());
    }

    @Test
    void testParseWithoutId() {
        String invalidCsv = "Id,firstName,lastName,salary\n" +
                ",Joe,Doe,invalid";
        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parse(file);
        });
        assertEquals("Employee ID cannot be empty. Line: ,Joe,Doe,invalid", exception.getMessage());
    }

    @Test
    void testparseWithNegativeSalary() {
        String invalidCsv = "Id,firstName,lastName,salary\n" +
                "123,Joe,Doe,-50000";

        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            csvParserService.parse(file);
        });

        assertEquals("Salary must be a positive number. Line: 123,Joe,Doe,-50000", exception.getMessage());
    }

    @Test
    void testparseWithMissingManager() {
        String invalidCsv = "Id,firstName,lastName,salary,managerId\n" +
                "123,Joe,Doe,60000\n" +
                "124,Martin,Chekov,45000,125"; // Manager 125 doesn't exist

        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csvParserService.parse(file);
        });

        assertTrue(exception.getMessage().contains("Manager not found for employee 124"));
    }

    @Test
    void testparseWithMultipleCEOs() {
        String invalidCsv = "Id,firstName,lastName,salary\n" +
                "123,Joe,Doe,60000\n" +
                "124,Martin,Chekov,60000";

        MockMultipartFile file = new MockMultipartFile("employees.csv", invalidCsv.getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            csvParserService.parse(file);
        });

        assertTrue(exception.getMessage().contains("Multiple employees without managers found"));
    }
}
