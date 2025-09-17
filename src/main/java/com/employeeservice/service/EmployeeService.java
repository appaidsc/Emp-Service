package com.employeeservice.service;

import com.employeeservice.dto.EmployeeCreateDto;
import com.employeeservice.dto.EmployeePersonalUpdateDto;
import com.employeeservice.entity.Department;
import com.employeeservice.entity.Employee;
import com.employeeservice.exception.ResourceNotFoundException;
import com.employeeservice.mapper.EmployeeMapper;
import com.employeeservice.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final KeycloakAdminClientService keycloakAdminClientService;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentService departmentService, KeycloakAdminClientService keycloakAdminClientService) {
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee with id " + id + " not found"));
    }



    @Transactional
    public Employee updatePersonalInfo(UUID id, EmployeePersonalUpdateDto employeeDetails) {
        Employee existingEmployee = getEmployeeById(id);
        EmployeeMapper.updateEntityFromPersonalInfoDto(existingEmployee, employeeDetails);
        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public Employee updateEmployeeSalary(UUID id, BigDecimal newSalary) {
        Employee existingEmployee = getEmployeeById(id);
        existingEmployee.setSalary(newSalary);
        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public Employee assignEmployeeToDepartment(UUID employeeId, UUID departmentId) {
        Employee employee = getEmployeeById(employeeId);
        Department department = departmentService.getDepartmentById(departmentId);
        employee.setDepartment(department);
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee with id " + id + " not found");
        }
        employeeRepository.deleteById(id);
    }

    public List<Employee> searchEmployees(String firstName, String lastName , UUID departmentId) {
        return employeeRepository.searchByCriteria(firstName, lastName, departmentId);
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Employee with email " + email + " not found"));
    }

    public List<Employee> findEmployeeBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        return employeeRepository.findBySalaryRange(minSalary, maxSalary);
    }

    public List<Employee> findEmployeesComplex(String email, String firstName, String lastName) {
        // Handle cases where parameters might be missing, if necessary
        if (email == null || firstName == null || lastName == null) {
            // Depending on requirements, you could throw an exception or return an empty list
            throw new IllegalArgumentException("All parameters (email, firstName, lastName) are required for this search.");
        }
        return employeeRepository.findComplex(email, firstName, lastName);
    }

    @Transactional
    public Employee createEmployee(Employee employee, EmployeeCreateDto employeeDto) {
        Department department = null;

        // Priority 1: Check for Department ID
        if (employeeDto.getDepartmentId() != null) {
            department = departmentService.getDepartmentById(employeeDto.getDepartmentId());
        }
        // Priority 2: Check for Department Name
        else if (employeeDto.getDepartmentName() != null && !employeeDto.getDepartmentName().isEmpty()) {
            department = departmentService.getDepartmentByName(employeeDto.getDepartmentName());
        }

        employee.setDepartment(department);
        Employee savedEmployee = employeeRepository.save(employee);
        try{
            keycloakAdminClientService.createKeycloakUser(savedEmployee);
        }
        catch (Exception e){
            throw new RuntimeException("Unable to save employee with id " + savedEmployee.getId(),e);
        }
        return savedEmployee;
    }

    @Transactional
    public List<Employee> bulkCreateEmployees(List<EmployeeCreateDto> employeeDtos) {
        // Step 1: Map DTOs to entities
        List<Employee> employeesToSave = employeeDtos.stream()
                .map(dto -> {
                    Employee employee = EmployeeMapper.fromCreateDto(dto);

                    Department department = null;
                    if (dto.getDepartmentId() != null) {
                        department = departmentService.getDepartmentById(dto.getDepartmentId());
                    } else if (dto.getDepartmentName() != null && !dto.getDepartmentName().isEmpty()) {
                        department = departmentService.getDepartmentByName(dto.getDepartmentName());
                    }
                    employee.setDepartment(department);
                    return employee;
                })
                .collect(Collectors.toList());

        // Step 2: Save all employees in DB
        List<Employee> insertedEmployees = employeeRepository.saveAll(employeesToSave);

        // Step 3: Create users in Keycloak
        try {
            for (Employee emp : insertedEmployees) {
                keycloakAdminClientService.createKeycloakUser(emp);
            }
        } catch (Exception e) {
            // rollback DB if any Keycloak creation fails
            throw new RuntimeException("Bulk Keycloak user creation failed. Rolling back employee inserts.", e);
        }

        return insertedEmployees;
    }
}