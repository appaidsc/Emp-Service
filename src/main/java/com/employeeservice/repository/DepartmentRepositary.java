package com.employeeservice.repository;

import com.employeeservice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRepositary extends JpaRepository<Department, UUID> {

}
