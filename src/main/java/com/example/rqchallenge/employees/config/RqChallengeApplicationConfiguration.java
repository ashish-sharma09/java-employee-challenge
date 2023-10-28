package com.example.rqchallenge.employees.config;

import com.example.rqchallenge.employees.service.EmployeeService;
import com.example.rqchallenge.employees.service.IEmployeeService;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class RqChallengeApplicationConfiguration {

    @Bean
    public IEmployeeService employeeService(RestTemplate restTemplate) {
        return new EmployeeService(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate(
            @Value("${employee.backend.service.uri}") String employeeServiceRootUri, HttpClient httpsClient) {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpsClient));
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(employeeServiceRootUri));
        return restTemplate;
    }

    @Bean
    public HttpClient httpsClient(
            @Value("${employee.backend.service.client.truststore}") Resource trustStore,
            @Value("${employee.backend.service.client.truststore.password}") String trustStorePassword
    ) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();

        return HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();
    }
}
