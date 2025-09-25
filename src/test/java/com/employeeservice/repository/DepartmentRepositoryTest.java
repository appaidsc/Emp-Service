package com.employeeservice.repository;

import com.employeeservice.entity.Department;
import com.sun.xml.txw2.annotation.XmlNamespace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;
    @AfterEach
    void tearDown() {
        // Clean up the database after each test
        departmentRepository.deleteAll();
    }

    @Test
    void itShoundfindByName() {
        // given
        Department department = new Department();
        department.setName("Human Resources");
        departmentRepository.save(department);

        //when
        Optional<Department> findDepartmet = departmentRepository.findByName("Human Resources");

        //then
        assertTrue(findDepartmet.isPresent());
        assertEquals("Human Resources", findDepartmet.get().getName());

    }

    @Test
    void itShouldNotFindByName_whenDepartmentDoesNotExist() {
        // --- ARRANGE ---
        // The database is clean, so no setup is needed.
        String nonExistentName = "Legal";

        // --- ACT ---
        // Call the method with a name that we know isn't there.
        Optional<Department> result = departmentRepository.findByName(nonExistentName);

        // --- ASSERT ---
        // Assert that the Optional is indeed empty. Do NOT call .get()!
        assertFalse(result.isPresent(), "The department should not be found, so the Optional should be empty.");

        // You could also write this assertion using AssertJ for more readable tests:
        assertThat(result).isNotPresent();
    }
}