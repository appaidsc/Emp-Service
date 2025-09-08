package com.employeeservice.dto;

import java.math.BigDecimal;

public class EmployeeSalaryUpdateDto {
    private BigDecimal salary;

    // Getters and setters
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
}