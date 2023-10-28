package com.example.rqchallenge.employees.model;

import lombok.Data;

@Data
public class Employee {
    private final String id;
    private final String name;
    private final String age;
    private final byte[] image;
}
