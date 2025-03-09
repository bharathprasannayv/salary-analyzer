package com.swissre.salaryanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private final List<UnderpaidManagerReport> underpaidManagers = new ArrayList<>();
    private final List<OverpaidManagerReport> overpaidManagers = new ArrayList<>();
    private final List<LongReportingLineEmployee> longReportingLineEmployees = new ArrayList<>();

    public List<UnderpaidManagerReport> getUnderpaidManagers() {
        return underpaidManagers;
    }

    public List<OverpaidManagerReport> getOverpaidManagers() {
        return overpaidManagers;
    }

    public List<LongReportingLineEmployee> getLongReportingLineEmployees() {
        return longReportingLineEmployees;
    }
}
