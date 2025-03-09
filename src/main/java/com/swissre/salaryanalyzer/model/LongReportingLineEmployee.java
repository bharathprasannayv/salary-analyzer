package com.swissre.salaryanalyzer.model;


public class LongReportingLineEmployee {
    private final Employee employee;
    private final int excessLength;

    public LongReportingLineEmployee(Employee employee, int excessLength) {
        this.employee = employee;
        this.excessLength = excessLength;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getExcessLength() {
        return excessLength;
    }
}
