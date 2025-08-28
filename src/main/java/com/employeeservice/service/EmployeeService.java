package com.employeeservice.service;


import com.employeeservice.entity.Employee;
import com.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository)
    {
        this.employeeRepository = employeeRepository;
    }

    public Employee insertEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getEmployees(){
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id);
        existing.setName(updated.getName());
        existing.setDepartment(updated.getDepartment());
        existing.setRole(updated.getRole());
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

}
