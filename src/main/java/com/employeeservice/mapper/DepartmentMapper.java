package com.employeeservice.mapper;

import com.employeeservice.dto.DepartmentCreateDto;
import com.employeeservice.dto.DepartmentDto;
import com.employeeservice.dto.DepartmentResponseDto;
import com.employeeservice.entity.Department;

public class DepartmentMapper {

    // For safe response
    public static DepartmentResponseDto toResponseDto(Department department) {
        DepartmentResponseDto dto = new DepartmentResponseDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        return dto;
    }

    // For DTO used in nested objects
    public static DepartmentDto toDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        return dto;
    }

    // For creating department from DTO
    public static Department fromCreateDto(DepartmentCreateDto dto) {
        Department department = new Department();
        department.setName(dto.getName());
        return department;
    }

    // Update entity from DTO
    public static void updateEntityFromDto(Department department, DepartmentCreateDto dto) {
        department.setName(dto.getName());
    }
}