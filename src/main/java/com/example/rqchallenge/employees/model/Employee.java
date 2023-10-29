package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {
    private final String id;
    private final String name;
    private final int salary;
    private final String age;
    private final byte[] image;
}
