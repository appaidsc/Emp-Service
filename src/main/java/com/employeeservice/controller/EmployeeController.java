package com.employeeservice.controller;



import com.employeeservice.entity.Employee;
import com.employeeservice.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // new line
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @PreAuthorize("hasRole('EMPLOYEE_READ') or hasRole('EMPLOYEE_WRITE')")
    @GetMapping("/get")
    public List<Employee> getEmployees(){
        return employeeService.getEmployees();
    }


    // Create
    @PreAuthorize("hasRole('EMPLOYEE_WRITE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.insertEmployee(employee);
    }

    // Read All
    @PreAuthorize("hasRole('EMPLOYEE_READ') or hasRole('EMPLOYEE_WRITE')")
    @GetMapping("/")
    public List<Employee> getAllEmployees() {
        return employeeService.getEmployees();
    }

    // Read One
    @PreAuthorize("hasRole('EMPLOYEE_READ') or hasRole('EMPLOYEE_WRITE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("ID not found");
        }
    }

    // Update
    @PreAuthorize("hasRole('EMPLOYEE_WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                   @RequestBody Employee employee) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("ID not found");
        }
    }

    // Delete
    @PreAuthorize("hasRole('EMPLOYEE_WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.getEmployeeById(id); // Throws if not found
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("ID not found");
        }
    }
    // POST: Batch insert employees
    @PostMapping("/batch")
    public List<Employee> addEmployees(@RequestBody List<Employee> employees) {
        return employeeService.saveAll(employees);
    }


}
