package com.example.rqchallenge.employees.utils;

public interface ResponseUtils {

    String SINGLE_EMPLOYEE = "{" +
            "  \"id\": \"1\"," +
            "  \"name\": \"Tiger Nixon\"," +
            "  \"salary\": 320800," +
            "  \"age\": \"61\"," +
            "  \"image\": \"\"" +
            "}";

    String NAME_SEARCH_EMPLOYEE_RESPONSE = "[" + SINGLE_EMPLOYEE + "]";

    String NAME_SEARCH_MULTIPLE_EMPLOYEE_RESPONSE = "[" +
                SINGLE_EMPLOYEE + "," +
                "{" +
                "  \"id\": \"2\"," +
                "  \"name\": \"Charles Dixon\"," +
                "  \"salary\": 264500," +
                "  \"age\": \"44\"," +
                "  \"image\": \"\"" +
                "}" +
            "]";





}
