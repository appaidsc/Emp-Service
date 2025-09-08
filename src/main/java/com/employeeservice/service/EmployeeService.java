package com.employeeservice.service;

import com.employeeservice.dto.EmployeePersonalUpdateDto;
import com.employeeservice.entity.Department;
import com.employeeservice.entity.Employee;
import com.employeeservice.exception.ResourceNotFoundException;
import com.employeeservice.mapper.EmployeeMapper;
import com.employeeservice.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee with id " + id + " not found"));
    }

    @Transactional
    public Employee createEmployee(Employee employee, UUID departmentId) {
        if (departmentId != null) {
            Department department = departmentService.getDepartmentById(departmentId);
            employee.setDepartment(department);
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updatePersonalInfo(UUID id, EmployeePersonalUpdateDto employeeDetails) {
        Employee existingEmployee = getEmployeeById(id);
        EmployeeMapper.updateEntityFromPersonalInfoDto(existingEmployee, employeeDetails);
        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public Employee updateEmployeeSalary(UUID id, BigDecimal newSalary) {
        Employee existingEmployee = getEmployeeById(id);
        existingEmployee.setSalary(newSalary);
        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public Employee assignEmployeeToDepartment(UUID employeeId, UUID departmentId) {
        Employee employee = getEmployeeById(employeeId);
        Department department = departmentService.getDepartmentById(departmentId);
        employee.setDepartment(department);
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee with id " + id + " not found");
        }
        employeeRepository.deleteById(id);
    }
}