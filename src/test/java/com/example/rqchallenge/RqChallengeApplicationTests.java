package com.example.rqchallenge;

import com.example.rqchallenge.employees.utils.ResponseUtils;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static com.example.rqchallenge.employees.utils.ResourceTestUtils.contentOf;
import static com.example.rqchallenge.employees.utils.ResourceTestUtils.resourcePath;
import static com.example.rqchallenge.employees.utils.ResponseUtils.CREATE_EMPLOYEE_BACKEND_RESPONSE_TEMPLATE;
import static com.example.rqchallenge.employees.utils.ResponseUtils.CREATE_EMPLOYEE_RESPONSE_TEMPLATE;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@ExtendWith(SpringExtension.class) // TODO check if we need this
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RqChallengeApplicationTests {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicHttpsPort()
                    .keystorePath(resourcePath("dummyKeystore.jks"))
                    .keystorePassword("changeit") // TODO pick from config file
                    .keyManagerPassword("changeit"))
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("employee.backend.service.uri", () -> wireMockServer.baseUrl() + "/api/v1");
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void dummyServiceTLSConfiguration() {

        String status = "{\"status\": \"success\"}";

        stubDummyServiceBehaviourWith("/status", status);

        RestAssured.baseURI = "https://localhost:" + wireMockServer.getHttpsPort();

        given()
                .trustStore(resourcePath("dummyKeystore.jks"), "changeit") // TODO pick from config file
                .when()
                .get("/status")
                .then()
                .body(Matchers.is(status));
    }

    @Test
    void getAllEmployees() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var expectedResponse = contentOf("expectedAllEmployees.json");
        var actualResponse = when().get("/").then().statusCode(200).extract().body().asPrettyString();

        assertEquals(expectedResponse, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchWithFirstNameToMatch() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/Tiger").then().statusCode(200).extract().body().asPrettyString();
        assertEquals(ResponseUtils.NAME_SEARCH_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchWithMatchingPartialNameIgnoringCase() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/tiger").then().statusCode(200).extract().body().asPrettyString();
        assertEquals(ResponseUtils.NAME_SEARCH_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchMatchingMultipleEmployees() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/ixon").then().statusCode(200).extract().body().asPrettyString();
        assertEquals(ResponseUtils.NAME_SEARCH_MULTIPLE_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeById() throws JSONException {
        stubDummyServiceBehaviourWith("/api/v1/employee/2", ResponseUtils.GET_EMPLOYEE_BY_ID_RESPONSE);
        var actualResponse = when().get("/2").then().statusCode(200).extract().body().asPrettyString();
        assertEquals(ResponseUtils.EMPLOYEE_2, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getHighestSalaryOfEmployees() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        String body = when().get("/highestSalary")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asString();
        assertThat(body).isEqualTo("365000");
    }

    @Test
    void getTop10HighestEarningEmployeeNames() throws IOException, JSONException {
        stubDummyServiceBehaviourWith("/api/v1/employees", contentOf("allEmployeesForTopTenQuery.json"));
        String actualResponse = when().get("/topTenHighestEarningEmployeeNames")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .body()
                .asPrettyString();
        var expectedResponse = contentOf("expectedAllEmployeesForTopTen.json");
        assertEquals(expectedResponse, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void createEmployee() throws JSONException {
        var name = "New Name";
        var salary  = "112233";
        var age = "23";

        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/create"))
                .withFormParam("name", equalTo(name))
                .withFormParam("salary", equalTo(salary))
                .withFormParam("age", equalTo(age))
                .withHeader("content-type", equalTo("application/x-www-form-urlencoded;charset=UTF-8"))
                .willReturn(aResponse()
                        .withHeader("content-type", "application/json")
                        .withBody(
                                responseFromTemplate(CREATE_EMPLOYEE_BACKEND_RESPONSE_TEMPLATE, name, salary, age)))
        );
        var createRequest = "{" +
                "\"name\":\"" + name + "\"," +
                "\"salary\":\"" + salary + "\"," +
                "\"age\":\"" + age + "\"" +
                "}";
        var actualResponse = given()
                .request()
                .header(new Header("Content-Type", "application/json"))
                .body(createRequest)
                .when()
                .post("/")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().asString();

        var expectedResponse = responseFromTemplate(CREATE_EMPLOYEE_RESPONSE_TEMPLATE, name, salary, age);

        assertEquals(expectedResponse, actualResponse, JSONCompareMode.STRICT);
    }

    private String responseFromTemplate(String template, String name, String salary, String age) {
        return template
                .replace("{name}", name)
                .replace("{salary}", salary)
                .replace("{age}", age);
    }

    private void stubDummyServiceBehaviourWith(String url, String responseBody) {
        wireMockServer.stubFor(
                WireMock.get(url)
                        .willReturn(aResponse().withBody(responseBody).withHeader("content-type", "application/json"))
        );
    }

    private void givenGetAllEmployeesStubbedBehaviour() throws IOException {
        stubDummyServiceBehaviourWith("/api/v1/employees", contentOf("allEmployeesFromDummyService.json"));
    }
}
