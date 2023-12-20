package com.venticonsulting.bookingservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WaApiClientConfig {

    @Value("${waapi.api.url}")
    private String waapiApiUrl;

    @Value("${waapi.api.bearer.token}")
    private String waapiBearerToken;

    @Bean
    public WebClient waapiWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(waapiApiUrl)
                .defaultHeader("accept", "application/json")
                .defaultHeader("authorization", "Bearer " + waapiBearerToken)
                .build();
    }

}
