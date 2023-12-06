package com.venticonsulting.authservice;

import com.venticonsulting.authservice.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }


}
