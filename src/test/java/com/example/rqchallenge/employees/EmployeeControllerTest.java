package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.controller.EmployeeController;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        // Given
        var employee1 = new Employee("1", "someEmployee1Name", 320800, "61", new byte[]{});
        var employee2 = new Employee("2", "someEmployee2Name", 223600, "41", new byte[]{});
        var employeeList = List.of(employee1,employee2);

        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        // Then
        assertThat(employeeController.getAllEmployees().getBody()).isEqualTo(employeeList);
    }

    @Test
    void getEmployeesByNameSearchMatchingAnEmployee() {
        // Given
        var matchingEmployee = new Employee("1", "someFirstName1 someLastName1", 320800, "61", new byte[]{});
        var nonMatchingEmployee = new Employee("2", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(List.of(matchingEmployee,nonMatchingEmployee));

        // Then
        assertThat(employeeController.getEmployeesByNameSearch("someFirstName1").getBody()).containsExactly(matchingEmployee);
    }

    @Test
    void getEmployeesByNameSearchShouldMatchNameIgnoringCase() {
        // Given
        var matchingEmployee = new Employee("1", "someFirstName1 someLastName1", 320800, "61", new byte[]{});
        var nonMatchingEmployee = new Employee("2", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(List.of(matchingEmployee,nonMatchingEmployee));

        // Then
        assertThat(employeeController.getEmployeesByNameSearch("somefirstname1").getBody()).containsExactly(matchingEmployee);
    }

    @Test
    void getEmployeesByIdSearch() {
        // Given
        var nonMatchingEmployee = new Employee("1", "someFirstName1 someLastName1", 320800, "61", new byte[]{});
        var matchingEmployee = new Employee("2", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(List.of(matchingEmployee,nonMatchingEmployee));

        // Then
        assertThat(employeeController.getEmployeeById("2").getBody()).isEqualTo(matchingEmployee);
    }

    @Test
    void errorResponseWhenThereAreMultipleEmployeesWithSameId() {
        // Given
        var nonMatchingEmployee = new Employee("1", "someFirstName1 someLastName1", 320800, "61", new byte[]{});
        var matchingEmployee = new Employee("1", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(List.of(matchingEmployee,nonMatchingEmployee));

        // Then
        assertThat(employeeController.getEmployeeById("1").getStatusCodeValue()).isEqualTo(500);
    }

    @Test
    void getHighestSalaryOfEmployees() {
        // Given
        int expectedResponse = 320800;
        var matchingEmployee = new Employee("1", "someFirstName1 someLastName1", expectedResponse, "61", new byte[]{});
        var nonMatchingEmployee = new Employee("2", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(List.of(matchingEmployee,nonMatchingEmployee));

        // Then
        assertThat(employeeController.getHighestSalaryOfEmployees().getBody()).isEqualTo(expectedResponse);
    }

}