package com.example.rqchallenge;

import com.example.rqchallenge.employees.utils.ResponseUtils;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

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
        registry.add("employee.backend.service.uri", wireMockServer::baseUrl);
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

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchWithFirstNameToMatch() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/Tiger").then().statusCode(200).extract().body().asPrettyString();
        JSONAssert.assertEquals(ResponseUtils.NAME_SEARCH_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchWithMatchingPartialNameIgnoringCase() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/tiger").then().statusCode(200).extract().body().asPrettyString();
        JSONAssert.assertEquals(ResponseUtils.NAME_SEARCH_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeByNameSearchMatchingMultipleEmployees() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/search/ixon").then().statusCode(200).extract().body().asPrettyString();
        JSONAssert.assertEquals(ResponseUtils.NAME_SEARCH_MULTIPLE_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    @Test
    void getEmployeeById() throws IOException, JSONException {
        givenGetAllEmployeesStubbedBehaviour();
        var actualResponse = when().get("/2").then().statusCode(200).extract().body().asPrettyString();
        JSONAssert.assertEquals(ResponseUtils.ID_SEARCH_EMPLOYEE_RESPONSE, actualResponse, JSONCompareMode.STRICT);
    }

    private String contentOf(String resourceName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resourceURI(resourceName))));
    }

    private void stubDummyServiceBehaviourWith(String url, String responseBody) {
        wireMockServer.stubFor(
                WireMock.get(url)
                        .willReturn(aResponse().withBody(responseBody).withHeader("content-type", "application/json"))
        );
    }

    private static String resourcePath(String resourceName) {
        return resourceURI(resourceName).getPath();
    }

    private static URI resourceURI(String resourceName) {
        try {
            return RqChallengeApplicationTests.class.getClassLoader().getResource(resourceName).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not find given resource: " + resourceName);
        }
    }

    private void givenGetAllEmployeesStubbedBehaviour() throws IOException {
        stubDummyServiceBehaviourWith("/employees", contentOf("allEmployeesFromDummyService.json"));
    }
}
