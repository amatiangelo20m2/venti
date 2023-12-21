package com.venticonsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan("com.venticonsulting.waapi.configuration")
public class BookingServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookingServiceApplication.class, args);
  }
}
