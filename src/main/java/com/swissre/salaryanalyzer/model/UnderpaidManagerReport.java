package com.swissre.salaryanalyzer.model;

public class UnderpaidManagerReport {
    private final Employee manager;
    private final double deficit;

    public UnderpaidManagerReport(Employee manager, double deficit) {
        this.manager = manager;
        this.deficit = deficit;
    }

    public Employee getManager() {
        return manager;
    }

    public double getDeficit() {
        return deficit;
    }
}
