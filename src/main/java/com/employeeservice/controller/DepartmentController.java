package com.employeeservice.controller;

import com.employeeservice.dto.DepartmentCreateDto;
import com.employeeservice.dto.DepartmentResponseDto;
import com.employeeservice.entity.Department;
import com.employeeservice.mapper.DepartmentMapper;
import com.employeeservice.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentService.getAllDepartments().stream()
                .map(DepartmentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable UUID id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(DepartmentMapper.toResponseDto(department));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponseDto> createDepartment(@RequestBody DepartmentCreateDto departmentDto) {
        // Convert DTO → entity
        Department department = DepartmentMapper.fromCreateDto(departmentDto);

        // Save department
        Department savedDepartment = departmentService.createDepartment(department);

        // Build location URI (assumes you have GET /departments/{id})
        URI location = URI.create(String.format("/departments/%s", savedDepartment.getId()));

        // Return 201 Created + Location header + response body
        return ResponseEntity
                .created(location)
                .body(DepartmentMapper.toResponseDto(savedDepartment));
    }


    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(
            @PathVariable UUID id,
            @RequestBody DepartmentCreateDto departmentDetails) {

        Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        return ResponseEntity.ok(DepartmentMapper.toResponseDto(updatedDepartment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}