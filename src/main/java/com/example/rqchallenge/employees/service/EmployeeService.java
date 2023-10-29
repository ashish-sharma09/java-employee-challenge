package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceException;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceNotFoundException;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.ResponseForDelete;
import com.example.rqchallenge.employees.service.model.ResponseForEmployee;
import com.example.rqchallenge.employees.service.model.ResponseForEmployees;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EmployeeService implements IEmployeeService {

    private final RestTemplate restTemplate;

    @Autowired
    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Employee> getAllEmployees() {
        var response = getForObject("/employees", ResponseForEmployees.class);
        return response.getData().stream().map(employeeData ->
                new Employee(
                        employeeData.getId(),
                        employeeData.getName(),
                        parsedSalaryOf(employeeData),
                        employeeData.getAge(),
                        employeeData.getImage()))
                .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(String id) {
        var response = getForObject("/employee/" + id, ResponseForEmployee.class);
        return employeeFrom(response);
    }

    @Override
    public Employee createEmployee(String name, String salary, String age) {
        try {
            var response = restTemplate.postForObject(
                    "/create",
                    buildRequestWithParams(name, salary, age),
                    ResponseForEmployee.class);
            return employeeFrom(response);
        } catch (HttpClientErrorException exception) {
            log.error("Error during backend service post call", exception);
            throw new EmployeeServiceException();
        }
    }

    @Override
    public void deleteEmployee(String id) {
        try {
            restTemplate.exchange("/delete/" + id, HttpMethod.DELETE, null, ResponseForDelete.class);
        } catch (HttpClientErrorException exception) {
            log.error("Error during backend service delete call", exception);
            throw new EmployeeServiceException();
        }
    }

    private HttpEntity<MultiValueMap<String, String>> buildRequestWithParams(String name, String salary, String age) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", name);
        map.add("salary", salary);
        map.add("age", age);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return request;
    }

    private Employee employeeFrom(ResponseForEmployee response) {
        return new Employee(
                response.getData().getId(),
                response.getData().getName(),
                parsedSalaryOf(response.getData()),
                response.getData().getAge(),
                response.getData().getImage());
    }

    private <T> T getForObject(String url, Class<T> objectType) {
        try {
            return restTemplate.getForObject(url, objectType);
        } catch(HttpClientErrorException exception) {
            log.error("Error during backend service get call", exception);
            if (exception.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new EmployeeServiceNotFoundException();
            } else {
                throw new EmployeeServiceException();
            }
        }
    }

    private int parsedSalaryOf(EmployeeData employeeData) {
        return Integer.parseInt(employeeData.getSalary());
    }
}
