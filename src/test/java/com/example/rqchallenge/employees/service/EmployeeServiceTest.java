package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.Response;
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

    @Test
    void getAllEmployees() {
        Response response = getResponse();
        Mockito.when(restTemplate.getForObject("/employees", Response.class)).thenReturn(response);

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

    private Response getResponse() {
        Response response = new Response();
        response.setStatus("success");
        var employeeData1 = new EmployeeData();
        employeeData1.setId("1");
        employeeData1.setName("Tiger Nixon");
        employeeData1.setSalary("320800");
        employeeData1.setAge("61");
        employeeData1.setImage("".getBytes());

        var employeeData2 = new EmployeeData();
        employeeData2.setId("2");
        employeeData2.setName("Foo Bar");
        employeeData2.setSalary("280800");
        employeeData2.setAge("41");
        employeeData2.setImage("".getBytes());

        response.setData(List.of(employeeData1, employeeData2));
        return response;
    }
}