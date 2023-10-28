package com.example.rqchallenge.employees.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeeData {
    private String id;
    @JsonProperty("employee_name")
    private String name;
    @JsonProperty("employee_salary")
    private String salary;
    @JsonProperty("employee_age")
    private String age;
    @JsonProperty("profile_image")
    private byte[] image;
}
