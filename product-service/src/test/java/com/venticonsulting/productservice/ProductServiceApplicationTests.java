package com.venticonsulting.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.assertions.Assertions;
import com.venticonsulting.productservice.dto.ProductRequest;
import com.venticonsulting.productservice.model.Product;
import com.venticonsulting.productservice.repository.ProductRepository;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

  /**
   * create a mongo container for tests from a mongo image (v. 4.4.2.)
   * is static because the access to this object will be static
   */
  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ProductRepository productRepository;

  static {
    mongoDBContainer.start();
  }


  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
    dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Test
  public void createProduct() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
      .contentType(MediaType.APPLICATION_JSON)
      .content(getProductAsJson())).andExpect(status().isCreated());

    Assertions.assertTrue(productRepository.findAll().size() == 1);


  }

  private String getProductAsJson() throws JsonProcessingException {

    ProductRequest aperol = ProductRequest
      .builder()
      .name("Aperol")
      .price(BigDecimal.valueOf(1200)).
      build();

    return objectMapper.writeValueAsString(aperol);
  }
}
