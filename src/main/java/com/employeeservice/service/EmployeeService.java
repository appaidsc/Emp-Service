package com.employeeservice.service;

import com.employeeservice.dto.EmployeeCreateDto;
import com.employeeservice.dto.EmployeePersonalUpdateDto;
import com.employeeservice.entity.Department;
import com.employeeservice.entity.Employee;
import com.employeeservice.exception.ResourceNotFoundException;
import com.employeeservice.mapper.EmployeeMapper;
import com.employeeservice.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);


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
        Employee employeeToDelete = getEmployeeById(id);
        String email = employeeToDelete.getEmail();

        try{
            keycloakAdminClientService.deleteKeycloakUser(email);
        }catch(Exception e){
            System.out.println("Could not delete user from Keycloak. " + e.getMessage());
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
            logger.error("Failed to create Keycloak user. Rolling back transaction.", e);
            throw new RuntimeException("Unable to save employee with id " + savedEmployee.getId(),e);
        }
        return savedEmployee;
    }

    @Transactional
    public List<Employee> bulkCreateEmployees(List<EmployeeCreateDto> employeeDtos) {
        // Step 1: Map all DTOs to employee entities
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

        // Step 2: Save all the new employees to the database in a single transaction
        List<Employee> savedEmployees = employeeRepository.saveAll(employeesToSave);

        // Step 3: Create a corresponding user in Keycloak for each new employee
        try {
            for (Employee employee : savedEmployees) {
                keycloakAdminClientService.createKeycloakUser(employee);
            }
        } catch (Exception e) {
            // If any Keycloak user creation fails, throw an exception to trigger a transaction rollback
            throw new RuntimeException("Bulk Keycloak user creation failed. Rolling back all employee inserts.", e);
        }

        return savedEmployees;
    }
}