package com.employeeservice.repository;

import com.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, EmployeeRepositoryCustom {
    boolean existsByDepartmentId(UUID departmentId);
    Optional<Employee> findByDepartmentId(UUID departmentId);
}
