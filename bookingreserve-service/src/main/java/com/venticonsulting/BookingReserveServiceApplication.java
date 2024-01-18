package com.venticonsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan("com.venticonsulting.branchconf.waapiconf.configuration")
public class BookingReserveServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookingReserveServiceApplication.class, args);
  }
}
