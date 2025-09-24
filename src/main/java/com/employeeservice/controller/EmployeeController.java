package com.employeeservice.controller;

import com.employeeservice.dto.*;
import com.employeeservice.entity.Employee;
import com.employeeservice.mapper.EmployeeMapper;
import com.employeeservice.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        Employee savedEmployee = employeeService.createEmployee(employee, employeeDto);
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

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponseDto>> searchEmployee(@RequestParam(required = false) String firstName,
                                                                    @RequestParam(required = false) String lastName ,
                                                                    @RequestParam(required = false) UUID departmentId) {
        List<Employee> employees = employeeService.searchEmployees(firstName, lastName, departmentId);
        List<EmployeeResponseDto> dtos = employees.stream()
                .map(EmployeeMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);

    }

    @GetMapping("/email/{email}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeByEmail(@PathVariable String email) {
        Employee employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(EmployeeMapper.toResponseDto(employee));
    }

    @GetMapping("/salary-range")
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesBySalaryRange(
            @RequestParam BigDecimal minSalary,
            @RequestParam BigDecimal maxSalary) {

        List<Employee> employees = employeeService.findEmployeeBySalaryRange(minSalary, maxSalary);
        List<EmployeeResponseDto> dtos = employees.stream()
                .map(EmployeeMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/search/complex")
    public ResponseEntity<List<EmployeeResponseDto>> searchEmployeesComplex(
            @RequestBody EmployeeSearchCriteria criteria) {

        List<EmployeeResponseDto> employees = employeeService.searchComplex(criteria);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<EmployeeResponseDto>> bulkEmployees(@RequestBody List<EmployeeCreateDto> employeeDtos) {
        List<Employee> savedEmployees = employeeService.bulkCreateEmployees(employeeDtos);

        List<EmployeeResponseDto> responseDtos = savedEmployees.stream()
                .map(EmployeeMapper::toResponseDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(responseDtos, HttpStatus.CREATED);
    }



}