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
        var employee = new Employee("2", "someFirstName2 someLastName2", 223600, "41", new byte[]{});

        when(employeeService.getEmployeeById("2")).thenReturn(employee);

        // Then
        assertThat(employeeController.getEmployeeById("2").getBody()).isEqualTo(employee);
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

    @Test
    void getTopTenHighestEarningEmployeeNames() {
        // Given
        var employee4 = new Employee("4", "Employee4", 4000, "41", new byte[]{});
        var employee2 = new Employee("2", "Employee2", 2000, "41", new byte[]{});
        var employee8 = new Employee("8", "Employee8", 8000, "41", new byte[]{});
        var employee3 = new Employee("3", "Employee3", 3000, "41", new byte[]{});
        var employee1 = new Employee("1", "Employee1", 1000, "61", new byte[]{});
        var employee5 = new Employee("5", "Employee5", 5000, "41", new byte[]{});
        var employee13 = new Employee("13", "Employee13", 40000, "41", new byte[]{});
        var employee7 = new Employee("7", "Employee7", 7000, "41", new byte[]{});
        var employee6 = new Employee("6", "Employee6", 6000, "41", new byte[]{});
        var employee9 = new Employee("9", "Employee9", 9000, "41", new byte[]{});
        var employee12 = new Employee("12", "Employee12", 30000, "41", new byte[]{});
        var employee10 = new Employee("10", "Employee10", 10000, "41", new byte[]{});
        var employee14 = new Employee("14", "Employee14", 50000, "41", new byte[]{});
        var employee15 = new Employee("15", "Employee15", 60000, "41", new byte[]{});
        var employee11 = new Employee("11", "Employee11", 20000, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(
                List.of(
                        employee4,
                        employee2,
                        employee8,
                        employee3,
                        employee1,
                        employee5,
                        employee13,
                        employee7,
                        employee6,
                        employee9,
                        employee12,
                        employee10,
                        employee14,
                        employee15,
                        employee11
                )
        );

        // Then
        var expectedResponse = List.of("Employee15","Employee14","Employee13","Employee12","Employee11",
                "Employee10","Employee9","Employee8","Employee7","Employee6");

        assertThat(employeeController.getTopTenHighestEarningEmployeeNames().getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void getAllEmployeeNamesWhenTotalEmployeesAreLessThan10() {
        // Given
        var employee4 = new Employee("4", "Employee4", 4000, "41", new byte[]{});
        var employee2 = new Employee("2", "Employee2", 2000, "41", new byte[]{});
        var employee6 = new Employee("8", "Employee8", 8000, "41", new byte[]{});
        var employee3 = new Employee("3", "Employee3", 3000, "41", new byte[]{});
        var employee1 = new Employee("1", "Employee1", 1000, "61", new byte[]{});
        var employee5 = new Employee("5", "Employee5", 5000, "41", new byte[]{});

        when(employeeService.getAllEmployees()).thenReturn(
                List.of(
                        employee4,
                        employee2,
                        employee3,
                        employee1,
                        employee5,
                        employee6
                )
        );

        // Then
        var expectedResponse = List.of("Employee8","Employee5","Employee4","Employee3","Employee2", "Employee1");

        assertThat(employeeController.getTopTenHighestEarningEmployeeNames().getBody()).isEqualTo(expectedResponse);
    }
}