package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceException;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceNotFoundException;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.ResponseForDelete;
import com.example.rqchallenge.employees.service.model.ResponseForEmployee;
import com.example.rqchallenge.employees.service.model.ResponseForEmployees;
import org.springframework.beans.factory.annotation.Autowired;
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("name", name);
        map.add("salary", salary);
        map.add("age", age);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        var response = restTemplate.postForObject("/create", request, ResponseForEmployee.class);
        return employeeFrom(response); //TODO check for null
    }

    @Override
    public void deleteEmployee(String id) {
        ResponseEntity<ResponseForDelete> response =
                restTemplate.exchange("/delete/" + id, HttpMethod.DELETE, null, ResponseForDelete.class);
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
