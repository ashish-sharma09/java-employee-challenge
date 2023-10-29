package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.model.EmployeeData;
import com.example.rqchallenge.employees.service.model.ResponseForEmployee;
import com.example.rqchallenge.employees.service.model.ResponseForEmployees;
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
        var response = restTemplate.getForObject("/employees", ResponseForEmployees.class);
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
    public Employee getEmployeeById(String id) {// TODO check for null or empty
        var response = restTemplate.getForObject("/employee/" + id, ResponseForEmployee.class);
        return new Employee(
                response.getData().getId(),
                response.getData().getName(),
                parsedSalaryOf(response.getData()),
                response.getData().getAge(),
                response.getData().getImage());
    }

    private int parsedSalaryOf(EmployeeData employeeData) {
        return Integer.parseInt(employeeData.getSalary());
    }

    @Override
    public boolean createEmployee(String name, String salary, String age) {
        return false;
    }
}
