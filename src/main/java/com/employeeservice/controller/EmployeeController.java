package com.employeeservice.controller;

import com.employeeservice.dto.*;
import com.employeeservice.entity.Employee;
import com.employeeservice.mapper.EmployeeMapper;
import com.employeeservice.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeService.getEmployees().stream()
                .map(EmployeeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable UUID id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(employee));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(@RequestBody EmployeeCreateDto employeeDto) {
        Employee employee = EmployeeMapper.fromCreateDto(employeeDto);
        Employee savedEmployee = employeeService.createEmployee(employee, employeeDto.getDepartmentId());
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(savedEmployee));
    }

    @PutMapping("/{id}/personal-info")
    public ResponseEntity<EmployeeResponseDto> updatePersonalInfo(
            @PathVariable UUID id,
            @RequestBody EmployeePersonalUpdateDto employeeDetails) {

        Employee updatedEmployee = employeeService.updatePersonalInfo(id, employeeDetails);
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(updatedEmployee));
    }

    @PutMapping("/{id}/salary")
    public ResponseEntity<EmployeeResponseDto> updateEmployeeSalary(
            @PathVariable UUID id,
            @RequestBody EmployeeSalaryUpdateDto payload) {

        Employee updatedEmployee = employeeService.updateEmployeeSalary(id, payload.getSalary());
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(updatedEmployee));
    }

    @PutMapping("/{employeeId}/department/{departmentId}")
    public ResponseEntity<EmployeeResponseDto> assignDepartment(
            @PathVariable UUID employeeId,
            @PathVariable UUID departmentId) {

        Employee updatedEmployee = employeeService.assignEmployeeToDepartment(employeeId, departmentId);
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(updatedEmployee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}