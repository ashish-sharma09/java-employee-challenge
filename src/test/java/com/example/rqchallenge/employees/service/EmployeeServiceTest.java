package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceException;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceNotFoundException;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.ResponseForDelete;
import com.example.rqchallenge.employees.service.model.ResponseForEmployee;
import com.example.rqchallenge.employees.service.model.ResponseForEmployees;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void getEmployeeByIdNotFound() {
        Mockito.when(restTemplate.getForObject("/employee/1", ResponseForEmployee.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(EmployeeServiceNotFoundException.class, () -> employeeService.getEmployeeById("1"));
    }

    @Test
    void getEmployeeByIdInternalServerError() {
        Mockito.when(restTemplate.getForObject("/employee/1", ResponseForEmployee.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(EmployeeServiceException.class, () -> employeeService.getEmployeeById("1"));
    }

    @Test
    void createEmployee() {
        ResponseForEmployee response = new ResponseForEmployee();
        EmployeeData employeeData = employee1Data();
        employeeData.setId("25");
        response.setData(employeeData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", employeeData.getName());
        map.add("salary", employeeData.getSalary());
        map.add("age", employeeData.getAge());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        Mockito.when(restTemplate.postForObject("/create", request, ResponseForEmployee.class))
                .thenReturn(response);

        var expectedEmployee = new Employee(
                                employeeData.getId(),
                                employeeData.getName(),
                                Integer.parseInt(employeeData.getSalary()),
                                employeeData.getAge(),
                                employeeData.getImage());

        assertThat(employeeService.createEmployee(employeeData.getName(), employeeData.getSalary(), employeeData.getAge()))
                .isEqualTo(expectedEmployee);
    }

    @Test
    void deleteEmployee() {
        var response = new ResponseForDelete();
        response.setStatus("Success");
        response.setMessage("successfully! deleted Record");
        Mockito.when(restTemplate.exchange("/delete/25", HttpMethod.DELETE, null, ResponseForDelete.class)).thenReturn(ResponseEntity.ok(response));

        employeeService.deleteEmployee("25");
        Mockito.verify(restTemplate).exchange("/delete/25", HttpMethod.DELETE, null, ResponseForDelete.class);
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