package com.example.rqchallenge;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
                    .keystorePath(resourcePath("dummyservice.jks"))
                    .keystorePassword("changeit") // TODO pick from config file
                    .keyManagerPassword("changeit"))
            .build();

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void contextLoads() { // TODO remove this later
    }

    @Test
    void checkDummyServiceTLSConfiguration() {

        String status = "        {" +
                "            \"status\": \"success\"" +
                "        }";

        stubDummyServiceBehaviourWith("/status", status);

        RestAssured.baseURI = "https://localhost:" + wireMockServer.getHttpsPort();

        given()
                .trustStore(resourcePath("dummyservice.jks"), "changeit") // TODO pick from config file
                .when()
                .get("/status")
                .then()
                .body(Matchers.is(status));
    }

    @Test
    void getAllEmployees() throws IOException {
        String employeesJson =
                new String(
                        Files.readAllBytes(Paths.get(resourceURI("allEmployeesFromDummyService.json")))
                );
        stubDummyServiceBehaviourWith("/employees", employeesJson);

        when().get("/").then().body(Matchers.is(employeesJson));
    }

    private void stubDummyServiceBehaviourWith(String url, String responseBody) {
        wireMockServer.stubFor(
                WireMock.get(url)
                        .willReturn(aResponse().withBody(responseBody))
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


}
