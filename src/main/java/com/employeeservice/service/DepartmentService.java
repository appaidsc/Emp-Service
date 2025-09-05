package com.employeeservice.service;

import com.employeeservice.entity.Department;
import com.employeeservice.repository.DepartmentRepositary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepositary departmentRepositary;

    public DepartmentService(DepartmentRepositary departmentRepositary) {
        this.departmentRepositary = departmentRepositary;
    }

    public List<Department> getAllDepartments() {
        return departmentRepositary.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepositary.findById(id)
                .orElseThrow(() -> new RuntimeException("Department with id " + id + " not found!"));
    }

    public Department createDepartment(Department department) {
        // Brother need to check if the deparment is present
        return departmentRepositary.save(department);
    }

    public Department updateDepartment(UUID id, Department departmentDetails) {
        Department existingDepartment = getDepartmentById(id);
        existingDepartment.setName(departmentDetails.getName());
        return departmentRepositary.save(existingDepartment);
    }
    public void deleteDepartment(UUID id) {
        boolean hasEmployee = departmentRepositary.existsById(id);
        if (hasEmployee) {
            throw new IllegalStateException("Cannot delete department with employees assigned.");
        }
        departmentRepositary.deleteById(id);
    }
}
