package com.employeeservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jdk.jfr.Enabled;

import java.util.Objects;
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String department;
    private String role;

    public Employee() {};

    public Employee(int i, String s, String engineering, String sde) {
        this.id = i;
        this.name = s;
        this.department = engineering;
        this.role = sde;
    }


    public long getId() {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDepartment()
    {
        return department;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getRole()
    {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(name, employee.name) && Objects.equals(department, employee.department) && Objects.equals(role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, department, role);
    }

    public void setRole(String role)
    {
        this.role = role;
    }
}
