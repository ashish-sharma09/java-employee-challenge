package com.example.rqchallenge.employees.utils;

public interface ResponseUtils {

    String EMPLOYEE_1 = "{" +
            "  \"id\": \"1\"," +
            "  \"name\": \"Tiger Nixon\"," +
            "  \"salary\": 320800," +
            "  \"age\": \"61\"," +
            "  \"image\": \"\"" +
            "}";


    String EMPLOYEE_2 = "{" +
            "  \"id\": \"2\"," +
            "  \"name\": \"Charles Dixon\"," +
            "  \"salary\": 264500," +
            "  \"age\": \"44\"," +
            "  \"image\": \"\"" +
            "}";

    String NAME_SEARCH_EMPLOYEE_RESPONSE = "[" + EMPLOYEE_1 + "]";

    String ID_SEARCH_EMPLOYEE_RESPONSE = "[" + EMPLOYEE_2 + "]";

    String NAME_SEARCH_MULTIPLE_EMPLOYEE_RESPONSE = "[" + EMPLOYEE_1 + "," + EMPLOYEE_2 + "]";





}
