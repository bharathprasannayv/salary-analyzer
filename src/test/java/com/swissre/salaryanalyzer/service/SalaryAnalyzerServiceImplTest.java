package com.swissre.salaryanalyzer.service;

import com.swissre.salaryanalyzer.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SalaryAnalyzerServiceImplTest {
    @Mock
    private CsvParser csvParser;

    @InjectMocks
    private SalaryAnalyzerServiceImpl salaryAnalyzerServiceImpl;

    @Test
    void testUnderpaidManager() throws IOException {
        // Create test employees from salaryanalyzer
        Employee ceo = new Employee("123", "Joe", "Doe", 60000, null);
        Employee manager = new Employee("124", "Martin", "Chekov", 45000, "123");
        Employee subordinate = new Employee("300", "Alice", "Hasacat", 50000, "124");

        // Build hierarchy
        ceo.addDirectSubordinate(manager);
        manager.addDirectSubordinate(subordinate);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("123", ceo);
        employees.put("124", manager);
        employees.put("300", subordinate);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "123,Joe,Doe,60000,\n" +
                                        "124,Martin,Chekov,45000,123\n" +
                                        "300,Alice,Hasacat,50000,124").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertEquals(1, result.getUnderpaidManagers().size());
        UnderpaidManagerReport underpaidReport = result.getUnderpaidManagers().get(0);
        assertEquals("124", underpaidReport.getManager().getId());
        assertEquals(15000, underpaidReport.getDeficit(), 0.01);

        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }

    @Test
    void testOverpaidManager() throws IOException {
        // Create test employees
        Employee ceo = new Employee("123", "Joe", "Doe", 92000, null);
        Employee manager = new Employee("124", "Martin", "Chekov", 76000, "123");
        Employee subordinate = new Employee("300", "Alice", "Hasacat", 50000, "124");

        // Build hierarchy
        ceo.addDirectSubordinate(manager);
        manager.addDirectSubordinate(subordinate);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("123", ceo);
        employees.put("124", manager);
        employees.put("300", subordinate);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "123,Joe,Doe,60000,\n" +
                                        "124,Martin,Chekov,76000,123\n" +
                                        "300,Alice,Hasacat,50000,124").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertEquals(1, result.getOverpaidManagers().size());
        OverpaidManagerReport overpaidReport = result.getOverpaidManagers().get(0);
        assertEquals("124", overpaidReport.getManager().getId());
        assertEquals(1000, overpaidReport.getExcess(), 0.01);

        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }

    @Test
    void testLongReportingLine() throws IOException {
        Employee ceo = new Employee("1", "CEO", "Last", 500000, null);
        Employee manager1 = new Employee("2", "Manager1", "Last", 400000, "1");
        Employee manager2 = new Employee("3", "Manager2", "Last", 300000, "2");
        Employee manager3 = new Employee("4", "Manager3", "Last", 225000, "3");
        Employee manager4 = new Employee("5", "Manager4", "Last", 175000, "4");
        Employee manager5 = new Employee("6", "Manager5", "Last", 130000, "5");
        Employee employee = new Employee("7", "Employee", "Last", 100000, "6");

        ceo.addDirectSubordinate(manager1);
        manager1.addDirectSubordinate(manager2);
        manager2.addDirectSubordinate(manager3);
        manager3.addDirectSubordinate(manager4);
        manager4.addDirectSubordinate(manager5);
        manager5.addDirectSubordinate(employee);

        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", ceo);
        employees.put("2", manager1);
        employees.put("3", manager2);
        employees.put("4", manager3);
        employees.put("5", manager4);
        employees.put("6", manager5);
        employees.put("7", employee);

        when(csvParser.parse(any())).thenReturn(employees);

        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "1,CEO,Last,500000,\n" +
                                        "2,Manager1,Last,400000,1\n" +
                                        "3,Manager2,Last,300000,2\n" +
                                        "4,Manager3,Last,225000,3\n" +
                                        "5,Manager4,Last,175000,4\n" +
                                        "6,Manager5,Last,130000,5\n" +
                                        "7,Employee,Last,100000,6").getBytes());

        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        assertEquals(2, result.getLongReportingLineEmployees().size());
        LongReportingLineEmployee longLineReport = result.getLongReportingLineEmployees().get(0);
        assertEquals("6", longLineReport.getEmployee().getId());
        assertEquals(1, longLineReport.getExcessLength());

        // Verify no salary issues
        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getOverpaidManagers().isEmpty());
    }

    @Test
    void testValidHierarchy() throws IOException {
        // Create test employees from salaryanalyzer
        Employee ceo = new Employee("123", "Joe", "Doe", 60000, null);
        Employee manager1 = new Employee("124", "Martin", "Chekov", 45000, "123");
        Employee manager2 = new Employee("125", "Bob", "Ronstad", 47000, "123");
        Employee subordinate1 = new Employee("300", "Alice", "Hasacat", 50000, "124");
        Employee subordinate2 = new Employee("305", "Brett", "Hardleaf", 34000, "300");

        // Build hierarchy
        ceo.addDirectSubordinate(manager1);
        ceo.addDirectSubordinate(manager2);
        manager1.addDirectSubordinate(subordinate1);
        subordinate1.addDirectSubordinate(subordinate2);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("123", ceo);
        employees.put("124", manager1);
        employees.put("125", manager2);
        employees.put("300", subordinate1);
        employees.put("305", subordinate2);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "123,Joe,Doe,60000,\n" +
                                        "124,Martin,Chekov,45000,123\n" +
                                        "125,Bob,Ronstad,47000,123\n" +
                                        "300,Alice,Hasacat,50000,124\n" +
                                        "305,Brett,Hardleaf,34000,300").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertEquals(1, result.getUnderpaidManagers().size());
        UnderpaidManagerReport underpaidReport = result.getUnderpaidManagers().get(0);
        assertEquals("124", underpaidReport.getManager().getId());
        assertEquals(15000, underpaidReport.getDeficit(), 0.01);

        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }

    @Test
    void testCEOAnalysis() throws IOException {
        // Create test employee (CEO)
        Employee ceo = new Employee("1", "CEO", "Last", 100000, null);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", ceo);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "1,CEO,Last,100000").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }

    @Test
    void testManagerWithMultipleDirectReports() throws IOException {
        // Create test employees
        Employee ceo = new Employee("1", "CEO", "Last", 450000, null);
        Employee manager = new Employee("2", "Manager", "Last", 311250, "1");
        Employee report1 = new Employee("3", "Report1", "Last", 300000, "2");
        Employee report2 = new Employee("4", "Report2", "Last", 225000, "2");
        Employee report3 = new Employee("5", "Report3", "Last", 175000, "2");
        Employee report4 = new Employee("6", "Report4", "Last", 130000, "2");

        // Build hierarchy
        ceo.addDirectSubordinate(manager);
        manager.addDirectSubordinate(report1);
        manager.addDirectSubordinate(report2);
        manager.addDirectSubordinate(report3);
        manager.addDirectSubordinate(report4);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", ceo);
        employees.put("2", manager);
        employees.put("3", report1);
        employees.put("4", report2);
        employees.put("5", report3);
        employees.put("6", report4);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "1,CEO,Last,450000\n" +
                                        "2,Manager,Last,311250,1\n" +
                                        "3,Report1,Last,300000,2\n" +
                                        "4,Report2,Last,225000,2\n" +
                                        "5,Report3,Last,175000,2\n" +
                                        "6,Report4,Last,130000,2").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }

    @Test
    void testManagerWithOneDirectReport() throws IOException {
        // Create test employees
        Employee ceo = new Employee("1", "CEO", "Last", 150000, null);
        Employee manager = new Employee("2", "Manager", "Last", 120000, "1");
        Employee report1 = new Employee("3", "Report1", "Last", 100000, "2");

        // Build hierarchy
        ceo.addDirectSubordinate(manager);
        manager.addDirectSubordinate(report1);

        // Create employee map
        Map<String, Employee> employees = new HashMap<>();
        employees.put("1", ceo);
        employees.put("2", manager);
        employees.put("3", report1);

        // Mock CSV parser
        when(csvParser.parse(any())).thenReturn(employees);

        // Create mock file
        MockMultipartFile file = new MockMultipartFile("employees.csv",
                ("Id,firstName,lastName,salary,managerId\n" +
                                        "1,CEO,Last,140000\n" +
                                        "2,Manager,Last,120000,1\n" +
                                        "3,Report1,Last,100000,2").getBytes());

        // Perform analysis
        AnalysisResult result = salaryAnalyzerServiceImpl.analyze(file);

        // Verify results
        assertTrue(result.getUnderpaidManagers().isEmpty());
        assertTrue(result.getOverpaidManagers().isEmpty());
        assertTrue(result.getLongReportingLineEmployees().isEmpty());
    }
}
