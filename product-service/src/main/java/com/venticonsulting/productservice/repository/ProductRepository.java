package com.venticonsulting.productservice.repository;

import com.venticonsulting.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
