package com.venticonsulting.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  /**
   *  when i create a bean, it will return with the name of the method (in this case webClient)
   * @return
   */
  @Bean
  public WebClient webClient(){
    return WebClient.builder().build();
  }
}
