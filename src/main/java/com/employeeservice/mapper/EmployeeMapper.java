package com.employeeservice.mapper;

import com.employeeservice.dto.*;
import com.employeeservice.entity.Employee;

public class EmployeeMapper {

    // For safe response (includes pinCode but not salary)
    public static EmployeeResponseDto toResponseDto(Employee employee) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setCity(employee.getCity());
        dto.setState(employee.getState());
        dto.setPinCode(employee.getPinCode());
        dto.setCountry(employee.getCountry());
        dto.setSalary(employee.getSalary());
        if (employee.getDepartment() != null) {
            dto.setDepartment(DepartmentMapper.toDto(employee.getDepartment()));
        }
        return dto;
    }

    // For creating employee from DTO
    public static Employee fromCreateDto(EmployeeCreateDto dto) {
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setCity(dto.getCity());
        employee.setState(dto.getState());
        employee.setPinCode(dto.getPinCode());
        employee.setCountry(dto.getCountry());
        employee.setSalary(dto.getSalary());
        return employee;
    }

    // Update entity from personal info DTO
    public static void updateEntityFromPersonalInfoDto(Employee employee, EmployeePersonalUpdateDto dto) {
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setCity(dto.getCity());
        employee.setState(dto.getState());
        employee.setPinCode(dto.getPinCode());
        employee.setCountry(dto.getCountry());
    }
}