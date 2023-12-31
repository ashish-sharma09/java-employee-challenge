package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceException;
import com.example.rqchallenge.employees.service.exception.EmployeeServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var filteredEmployees = findEmployeesByFilter(
                employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()));

        if (!filteredEmployees.isEmpty()) {
            return ResponseEntity.ok(filteredEmployees);
        } else {
            log.info("No Employees found with name: {}", searchString);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        var employees = sortedEmployeesBySalaryInDescendingOrder().collect(Collectors.toList());
        return ResponseEntity.ok(employees.get(0).getSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        var sortedEmployees = sortedEmployeesBySalaryInDescendingOrder()
                .map(Employee::getName)
                .collect(Collectors.toList());

        if (sortedEmployees.size() > 10) {
            return ResponseEntity.ok(sortedEmployees.subList(0, 10));
        } else {
            return ResponseEntity.ok(sortedEmployees);
        }
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        return ResponseEntity.ok(employeeService.createEmployee(
                    employeeInput.get("name").toString(),
                    employeeInput.get("salary").toString(),
                    employeeInput.get("age").toString()
                ));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(employee.getName());
    }

    @ExceptionHandler({EmployeeServiceNotFoundException.class})
    public ResponseEntity handleException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({EmployeeServiceException.class})
    public ResponseEntity handleAnyOtherDownstreamException() {
        return ResponseEntity.internalServerError().build();
    }

    private Stream<Employee> sortedEmployeesBySalaryInDescendingOrder() {
        return allEmployees()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()));
    }

    private List<Employee> findEmployeesByFilter(Predicate<Employee> employeePredicate) {
        return allEmployees().stream()
                .filter(employeePredicate)
                .collect(Collectors.toList());
    }

    private List<Employee> allEmployees() {
        var allEmployees = employeeService.getAllEmployees();

        if (allEmployees != null) {
            return allEmployees;
        } else {
            return Collections.emptyList();
        }
    }
}
