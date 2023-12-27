package com.venticonsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan("com.venticonsulting.waapi.configuration")
public class BookingReserveServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookingReserveServiceApplication.class, args);
  }
}
