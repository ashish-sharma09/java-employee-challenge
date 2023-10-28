package com.example.rqchallenge.employees.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceTestUtils {

    public static String contentOf(String resourceName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(resourceURI(resourceName))));
    }

    public static String resourcePath(String resourceName) {
        return resourceURI(resourceName).getPath();
    }

    public static URI resourceURI(String resourceName) {
        try {
            return ResourceTestUtils.class.getClassLoader().getResource(resourceName).toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not find given resource: " + resourceName);
        }
    }
}
