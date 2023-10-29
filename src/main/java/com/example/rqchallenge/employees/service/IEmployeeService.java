package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.Employee;

import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(String id);
    Employee createEmployee(String name, String salary, String age);
}
