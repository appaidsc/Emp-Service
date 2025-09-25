package com.employeeservice.service;

import com.employeeservice.entity.Department;
import com.employeeservice.repository.DepartmentRepository;
import com.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void createDepartment_shouldSaveDepartment_whenDepartmentNameIsUnique() {
        // Arrange
        Department department = new Department();
        department.setName("Engineering");

        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        // Act
        Department savedDepartment = departmentService.createDepartment(department);

        // Assert
        assertNotNull(savedDepartment);
        assertEquals("Engineering", savedDepartment.getName());
        verify(departmentRepository, times(1)).findByName("Engineering");
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void createDepartment_shouldThrowException_whenDepartmentNameAlreadyExists() {
        // Arrange
        Department department = new Department();
        department.setName("Engineering");

        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.of(department));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.createDepartment(department);
        });

        assertEquals("Department with name Engineering already exists!", exception.getMessage());
        verify(departmentRepository, times(1)).findByName("Engineering");
        verify(departmentRepository, never()).save(department);
    }
}