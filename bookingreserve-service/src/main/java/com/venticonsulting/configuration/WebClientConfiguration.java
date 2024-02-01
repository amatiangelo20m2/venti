package com.venticonsulting.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${waapi.api.url}")
    private String waapiApiUrl;

    @Value("${waapi.api.bearer.token}")
    private String waapiBearerToken;

    @Value("${dashboard.microservice.name}")
    private String dashboardServiceName;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    // Bean for WebClient to access external service without load balancing
    @Bean
    public WebClient.Builder externalWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient dashboardServiceWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder.baseUrl("http://" + dashboardServiceName)
                .defaultHeader("accept", "application/json")
                .build();
    }

    @Bean
    public WebClient waapiWebClient(WebClient.Builder externalWebClientBuilder) {
        return externalWebClientBuilder.baseUrl(waapiApiUrl)
                .defaultHeader("accept", "application/json")
                .defaultHeader("authorization", "Bearer " + waapiBearerToken)
                .build();
    }
}
