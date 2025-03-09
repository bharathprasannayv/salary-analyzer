package com.swissre.salaryanalyzer.model;


public class OverpaidManagerReport {
    private final Employee manager;
    private final double excess;

    public OverpaidManagerReport(Employee manager, double excess) {
        this.manager = manager;
        this.excess = excess;
    }

    public Employee getManager() {
        return manager;
    }

    public double getExcess() {
        return excess;
    }


}
