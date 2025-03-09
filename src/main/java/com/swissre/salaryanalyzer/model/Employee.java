package com.swissre.salaryanalyzer.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final String managerId;
    private Employee manager;
    private final List<Employee> directSubordinates;

    public Employee(String id, String firstName, String lastName, double salary, String managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
        this.directSubordinates = new ArrayList<>();
    }

    public List<Employee> getDirectSubordinates() {
        return directSubordinates;
    }

    public void addDirectSubordinate(Employee subordinate) {
        directSubordinates.add(subordinate);
    }

    public String getId() {
        return id;
    }

    public double getSalary() {
        return salary;
    }

    public String getManagerId() {
        return managerId;
    }
}
