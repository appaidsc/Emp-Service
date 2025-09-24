package com.employeeservice.repository;

import com.employeeservice.dto.EmployeeSearchCriteria;
import com.employeeservice.entity.Department;
import com.employeeservice.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Employee> searchByCriteria(String firstName, String lastName, UUID departmentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class); // Consistent naming
        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        if (departmentId != null) {
            predicates.add(cb.equal(employee.get("department").get("id"), departmentId));
        }
        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Employee> findByEmail(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class); // Consistent naming

        Predicate emailPredicate = cb.equal(employee.get("email"), email); // Descriptive name
        cq.where(emailPredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Employee> findBySalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class); // Consistent naming

        Predicate salaryRangePredicate = cb.between(employee.get("salary"), minSalary, maxSalary); // Descriptive name
        cq.where(salaryRangePredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Employee> findComplex(EmployeeSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class);
        List<Predicate> predicates = new ArrayList<>();

        if(criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("firstName")), "%" + criteria.getFirstName().toLowerCase() + "%"));
        }
        if(criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("lastName")), "%" + criteria.getLastName().toLowerCase()+ "%"));
        }
        if(criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("email")), "%" + criteria.getEmail().toLowerCase()+ "%"));
        }
        if(criteria.getCity() != null && !criteria.getCity().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("city")), criteria.getCity().toLowerCase()+ "%"));
        }
        if(criteria.getState() != null && !criteria.getState().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("state")), criteria.getState().toLowerCase()+ "%"));
        }
        if(criteria.getCountry() != null && !criteria.getCountry().isEmpty()) {
            predicates.add(cb.like(cb.lower(employee.get("country")), criteria.getCountry().toLowerCase()+ "%"));
        }
        if(criteria.getSalaryFrom() != null && criteria.getSalaryTo() != null){
            predicates.add(cb.between(employee.get("salary"), criteria.getSalaryFrom(), criteria.getSalaryTo()));
        } else if (criteria.getSalaryFrom() != null) {
            predicates.add(cb.greaterThan(employee.get("salary"), criteria.getSalaryFrom()));
        } else if (criteria.getSalaryTo() != null) {
            predicates.add(cb.lessThan(employee.get("salary"), criteria.getSalaryTo()));
        }
        if (criteria.getDepartmentName() != null && !criteria.getDepartmentName().isEmpty()) {
            Join<Employee, Department> departmentJoin = employee.get("department");
            predicates.add(cb.like(cb.lower(departmentJoin.get("name")), "%" + criteria.getDepartmentName().toLowerCase()+ "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();

    }

}