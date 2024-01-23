package com.venticonsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan("com.venticonsulting.branchconf.waapiconf.configuration")
//@EntityScan("")
//@EnableJpaRepositories("com.venticonsulting.branchconf.bookingconf.repository")
public class BookingReserveServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookingReserveServiceApplication.class, args);
  }
}
