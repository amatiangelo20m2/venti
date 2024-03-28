package com.ventimetriconsulting.branch.configuration.waapiconf.service;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     *  when bean is created it will return with the name of the method (in this case webClient)
     *
     * @LoadBalanced -> will add a load balancing capabilities to my web client builder
     *
     *
     * @return
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
