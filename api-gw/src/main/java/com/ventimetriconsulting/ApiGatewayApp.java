package com.ventimetriconsulting;

import com.ventimetriconsulting.filter.AuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApp {
  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApp.class, args);
  }

  @Bean
  public AuthenticationFilter authenticationFilter() {
    return new AuthenticationFilter();
  }
}
