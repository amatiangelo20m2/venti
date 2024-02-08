package com.ventimetriconsulting.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/sign-up",
            "/api/auth/retrieve",
            "/api/auth/sign-in",
            "/api/auth/sign-in-with-token",
            "/api/auth/delete",
            "/api/auth/update",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
