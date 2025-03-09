package com.swissre.salaryanalyzer.controller;

import com.swissre.salaryanalyzer.model.AnalysisResult;
import com.swissre.salaryanalyzer.service.SalaryAnalyzerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@ExtendWith(MockitoExtension.class)
public class SalaryAnalyzerControllerTest {
    @Mock
    private SalaryAnalyzerServiceImpl salaryAnalyzerServiceImpl; // Mocking the service dependency

    @InjectMocks
    private SalaryAnalyzerController controller; // Injecting mocks into the controller

    @Test
    void analyze_ShouldReturnAnalysisResult_WhenFileIsValid() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, world".getBytes());
        AnalysisResult expectedResult = new AnalysisResult(); // Assume a valid result object
        when(salaryAnalyzerServiceImpl.analyze(file)).thenReturn(expectedResult);

        // When
        AnalysisResult actualResult = controller.analyze(file);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
        verify(salaryAnalyzerServiceImpl, times(1)).analyze(file); // Verify service is called once
    }

    @Test
    void analyze_ShouldThrowException_WhenFileIsEmpty() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.analyze(emptyFile);
        });

        assertEquals("File cannot be empty", exception.getMessage());
        verifyNoInteractions(salaryAnalyzerServiceImpl); // Ensure service is not called
    }

    @Test
    void analyze_ShouldThrowRuntimeException_WhenServiceFails() throws Exception {
        // Given
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, world".getBytes());
        when(salaryAnalyzerServiceImpl.analyze(file)).thenThrow(new RuntimeException("Service error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.analyze(file);
        });

        assertEquals("Error processing file", exception.getMessage());
        verify(salaryAnalyzerServiceImpl, times(1)).analyze(file); // Ensure service was called once
    }
}
