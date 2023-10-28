package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
        Response response = restTemplate.getForObject("/employees", Response.class);
        return response.getData().stream().map(employeeData ->
                new Employee(
                        employeeData.getId(),
                        employeeData.getName(),
                        Integer.parseInt(employeeData.getSalary()),
                        employeeData.getAge(),
                        employeeData.getImage()))
                .collect(Collectors.toList());
    }
}
