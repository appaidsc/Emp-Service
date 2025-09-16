package com.employeeservice.repository;

import com.employeeservice.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
    public List<Employee> findComplex(String email, String firstName, String lastName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class); // Consistent naming

        Predicate firstNamePredicate = cb.equal(employee.get("firstName"), firstName);
        Predicate lastNamePredicate = cb.equal(employee.get("lastName"), lastName);
        Predicate nameOrPredicate = cb.or(firstNamePredicate, lastNamePredicate);

        Predicate emailPredicate = cb.equal(employee.get("email"), email);

        Predicate finalPredicate = cb.and(nameOrPredicate, emailPredicate);
        cq.where(finalPredicate);

        return entityManager.createQuery(cq).getResultList();
    }
}