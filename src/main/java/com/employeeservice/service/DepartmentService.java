package com.employeeservice.service;

import com.employeeservice.dto.DepartmentCreateDto;
import com.employeeservice.entity.Department;
import com.employeeservice.exception.ResourceNotFoundException;
import com.employeeservice.mapper.DepartmentMapper;
import com.employeeservice.repository.DepartmentRepository;
import com.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department with id " + id + " not found!"));
    }

    public Department createDepartment(Department department) {
        departmentRepository.findByName(department.getName()).ifPresent(d -> {
            throw new RuntimeException("Department with name " + department.getName() + " already exists!");
        });
        return departmentRepository.save(department);
    }

    public Department updateDepartment(UUID id, DepartmentCreateDto departmentDetails) {
        Department existingDepartment = getDepartmentById(id);
        DepartmentMapper.updateEntityFromDto(existingDepartment, departmentDetails);
        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department with id " + id + " not found!");
        }

        boolean hasEmployees = employeeRepository.existsByDepartmentId(id);
        if (hasEmployees) {
            throw new IllegalStateException("Cannot delete department: Employees are still assigned to it.");
        }
        departmentRepository.deleteById(id);
    }
}