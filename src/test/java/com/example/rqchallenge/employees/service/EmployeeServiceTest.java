package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.ResponseForEmployee;
import com.example.rqchallenge.employees.service.model.ResponseForEmployees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    RestTemplate restTemplate;

    private IEmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(restTemplate);
    }
    //TODO change url to include /api/v1
    @Test
    void getAllEmployees() {
        ResponseForEmployees response = getResponse();
        Mockito.when(restTemplate.getForObject("/employees", ResponseForEmployees.class)).thenReturn(response);

        var expectedEmployees = response.getData().stream().map(employeeData ->
                        new Employee(
                                employeeData.getId(),
                                employeeData.getName(),
                                Integer.parseInt(employeeData.getSalary()),
                                employeeData.getAge(),
                                employeeData.getImage())
                ).
                collect(Collectors.toList());

        assertThat(employeeService.getAllEmployees()).containsAll(expectedEmployees);
    }

    @Test
    void getEmployeeById() {
        ResponseForEmployee response = new ResponseForEmployee();
        EmployeeData employeeData = employee1Data();
        response.setData(employeeData);
        Mockito.when(restTemplate.getForObject("/employee/1", ResponseForEmployee.class)).thenReturn(response);

        var expectedEmployee = new Employee(
                                employeeData.getId(),
                                employeeData.getName(),
                                Integer.parseInt(employeeData.getSalary()),
                                employeeData.getAge(),
                                employeeData.getImage());

        assertThat(employeeService.getEmployeeById("1")).isEqualTo(expectedEmployee);
    }

    private ResponseForEmployees getResponse() {
        ResponseForEmployees response = new ResponseForEmployees();
        EmployeeData employeeData1 = employee1Data();

        var employeeData2 = new EmployeeData();
        employeeData2.setId("2");
        employeeData2.setName("Foo Bar");
        employeeData2.setSalary("280800");
        employeeData2.setAge("41");
        employeeData2.setImage("".getBytes());

        response.setData(List.of(employeeData1, employeeData2));
        return response;
    }

    private EmployeeData employee1Data() {
        var employeeData1 = new EmployeeData();
        employeeData1.setId("1");
        employeeData1.setName("Tiger Nixon");
        employeeData1.setSalary("320800");
        employeeData1.setAge("61");
        employeeData1.setImage("".getBytes());
        return employeeData1;
    }
}