package com.employeeservice.controller;


import com.employeeservice.entity.Employee;
import com.employeeservice.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Endpoint for creating a new employee (typically for HR/Director)
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    // Endpoint for an employee to update their own personal info (not salary or department)
    @PutMapping("/{id}/personal-info")
    public ResponseEntity<Employee> updatePersonalInfo(@PathVariable UUID id, @RequestBody Employee employeeDetails) {
        return ResponseEntity.ok(employeeService.updatePersonalInfo(id, employeeDetails));
    }

    // Endpoint specifically for updating salary (for Accounts Manager/Director)
    @PutMapping("/{id}/salary")
    public ResponseEntity<Employee> updateEmployeeSalary(@PathVariable UUID id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal newSalary = payload.get("salary");
        if (newSalary == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employeeService.updateEmployeeSalary(id, newSalary));
    }

    // Endpoint for assigning an employee to a new department (for HR/Director)
    @PutMapping("/{employeeId}/department/{departmentId}")
    public ResponseEntity<Employee> assignDepartment(@PathVariable UUID employeeId, @PathVariable UUID departmentId) {
        return ResponseEntity.ok(employeeService.assignEmployeeToDepartment(employeeId, departmentId));
    }

    // Endpoint for deleting an employee (typically for Director)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
