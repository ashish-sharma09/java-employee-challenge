package com.example.rqchallenge.employees.service.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseForEmployees {
    private List<EmployeeData> data;
}
