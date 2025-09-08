package com.employeeservice.dto;

import java.util.UUID;

public class DepartmentResponseDto {
    private UUID id;
    private String name;

    // Constructors
    public DepartmentResponseDto() {}

    public DepartmentResponseDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}