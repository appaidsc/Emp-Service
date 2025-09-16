package com.employeeservice.repository;

import com.employeeservice.dto.EmployeeResponseDto;
import com.employeeservice.entity.Employee;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepositoryCustom {

    List<Employee> searchByCriteria(String firstName, String lastName, UUID departmentId);
    List<Employee> findByEmail(String email);

    List<Employee> findBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary);

    List<Employee> findComplex(String email, String firstName, String lastName);

}
