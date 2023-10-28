package com.example.rqchallenge.employees.service.model;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private String status;
    private List<EmployeeData> data;
}
