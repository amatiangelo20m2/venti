package com.ventimetriconsulting.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
    // all request made to retrieve static resources
    // (like the one for the eureka html and css static resources) are allowed without authentication
    serverHttpSecurity
      .csrf()
      .disable()
      .authorizeExchange(
        exchange -> exchange.pathMatchers("/eureka/**").permitAll().anyExchange().authenticated()
      ).oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

    return serverHttpSecurity.build();

  }


}