package com.ventimetriconsulting.inventario.service;

import com.ventimetriconsulting.inventario.entity.Inventario;
import com.ventimetriconsulting.inventario.repository.InventarioRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Inventario addProductConsumption(long productId,
                                            int consumedQuantity,
                                            LocalDate date) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        Inventario inventario = Inventario.builder()
                .inventarioId(0)
                .product(product)
                .consumedQuantity(consumedQuantity)
                .date(date)
                .build();

        return inventarioRepository.save(inventario);
    }

    @Transactional
    public Inventario updateProductConsumption(long inventarioId, int newConsumedQuantity) {
        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid inventario ID: " + inventarioId));

        inventario.setConsumedQuantity(newConsumedQuantity);
        return inventarioRepository.save(inventario);
    }

    @Transactional
    public void deleteProductConsumption(long inventarioId) {
        if (!inventarioRepository.existsById(inventarioId)) {
            throw new IllegalArgumentException("Invalid inventario ID: " + inventarioId);
        }
        inventarioRepository.deleteById(inventarioId);
    }

    public Optional<Inventario> findInventarioById(long inventarioId) {
        return inventarioRepository.findById(inventarioId);
    }

    public List<Inventario> findAllInventarios() {
        return inventarioRepository.findAll();
    }
}
