package com.example.rqchallenge;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import lombok.SneakyThrows;
import net.bytebuddy.pool.TypePool;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class) // TODO check if we need this
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RqChallengeApplicationTests {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicHttpsPort()
                    .keystorePath(resourcePath("dummyservice.jks"))
                    .keystorePassword("changeit")
                    .keyManagerPassword("changeit"))
            .build();

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() { // TODO remove this later
    }

    @Test
    void checkDummyServiceTLSConfiguration() {

        String status = "        {" +
                "            \"status\": \"success\"" +
                "        }";

        wireMockServer.stubFor(
                WireMock.get("/status")
                        .willReturn(aResponse().withBody(status))
        );

        RestAssured.baseURI = "https://localhost:" + wireMockServer.getHttpsPort();

        given()
                .trustStore(resourcePath("dummyservice.jks"), "changeit")
                .when()
                .get("/status")
                .then()
                .body(Matchers.is(status));
    }

    private static String resourcePath(String resourceName) {
        try {
            return RqChallengeApplicationTests.class.getClassLoader().getResource(resourceName).toURI().getPath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not find given resource: " + resourceName);
        }
    }

}
