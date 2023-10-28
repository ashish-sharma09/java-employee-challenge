package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.controller.EmployeeController;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    IEmployeeService employeeService;

    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        employeeController = new EmployeeController(employeeService);
    }

    @Test
    void getAllEmployeesAsProvidedByBackendEmployeeService() {
        List<Employee> expectedEmployeeList = List.of(
                new Employee("1", "someEmployee1Name", 320800, "61", new byte[]{}),
                new Employee("2", "someEmployee2Name", 223600, "41", new byte[]{})
        );

        Mockito.when(employeeService.getAllEmployees()).thenReturn(expectedEmployeeList);

        assertThat(employeeController.getAllEmployees().getBody()).isEqualTo(expectedEmployeeList);
    }



}