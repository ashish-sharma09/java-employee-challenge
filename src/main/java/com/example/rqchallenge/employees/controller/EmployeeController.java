package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
        var employees = sortedEmployeesBySalaryInDescendingOrder().collect(Collectors.toList());
        return ResponseEntity.ok(employees.get(0).getSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        var sortedEmployees = sortedEmployeesBySalaryInDescendingOrder()
                .map(Employee::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedEmployees.subList(0, 10));
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
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
        return employeeService.getAllEmployees();
    }
}
