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

    String NAME_SEARCH_MULTIPLE_EMPLOYEE_RESPONSE = "[" + EMPLOYEE_1 + "," + EMPLOYEE_2 + "]";

    String GET_EMPLOYEE_BY_ID_RESPONSE = "{" +
            " \"status\": \"success\"," +
            " \"data\": {" +
            "      \"id\": \"2\"," +
            "      \"employee_name\": \"Charles Dixon\"," +
            "      \"employee_salary\": \"264500\"," +
            "      \"employee_age\": \"44\"," +
            "      \"profile_image\": \"\"" +
            "  }" +
            "}";

    String CREATE_EMPLOYEE_BACKEND_RESPONSE_TEMPLATE = "{" +
            "    \"status\": \"success\"," +
            "    \"data\": {" +
            "        \"employee_name\": \"{name}\"," +
            "        \"employee_salary\": \"{salary}\"," +
            "        \"employee_age\": \"{age}\"," +
            "        \"id\": 25" +
            "    }" +
            "}";

    String CREATE_EMPLOYEE_RESPONSE_TEMPLATE = "{" +
            "  \"id\": \"25\"," +
            "  \"name\": \"{name}\"," +
            "  \"salary\": {salary}," +
            "  \"age\": \"{age}\"" +
            "}";
}
