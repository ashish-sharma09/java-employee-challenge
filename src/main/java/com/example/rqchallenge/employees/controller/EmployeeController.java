package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

@Slf4j
@RestController
public class EmployeeController implements IEmployeeController {

    private final IEmployeeService employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(allEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok(findEmployeesByFilter(
                employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase())));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        List<Employee> employeeByFilter = findEmployeesByFilter(employee -> employee.getId().equals(id));

        if (employeeByFilter.size() > 1) {
            log.error("Multiple employees found with id: {}", id);
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(employeeByFilter.get(0));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        var employees = allEmployees()
                .stream()
                .sorted(comparingInt(Employee::getSalary))
                .collect(Collectors.toList());

        return ResponseEntity.ok(employees.get(employees.size()-1).getSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return null;
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }

    private List<Employee> findEmployeesByFilter(Predicate<Employee> employeePredicate) {
        return allEmployees().stream()
                .filter(employeePredicate)
                .collect(Collectors.toList());
    }

    private List<Employee> allEmployees() {
        return employeeService.getAllEmployees();
    }
}
