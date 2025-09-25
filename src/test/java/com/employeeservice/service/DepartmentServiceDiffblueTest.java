package com.employeeservice.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.employeeservice.entity.Department;
import com.employeeservice.repository.DepartmentRepository;
import com.employeeservice.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {DepartmentService.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class DepartmentServiceDiffblueTest {
    @MockitoBean
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentService departmentService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    /**
     * Test {@link DepartmentService#getAllDepartments()}.
     *
     * <ul>
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link DepartmentService#getAllDepartments()}
     */
    @Test
    @DisplayName("Test getAllDepartments(); then return Empty")
    void testGetAllDepartments_thenReturnEmpty() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Department> actualAllDepartments = departmentService.getAllDepartments();

        // Assert
        verify(departmentRepository).findAll();
        assertTrue(actualAllDepartments.isEmpty());
    }
}
