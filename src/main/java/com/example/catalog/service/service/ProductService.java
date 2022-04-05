package com.example.catalog.service.service;

import com.example.catalog.service.model.Product;
import com.example.catalog.service.model.ProductInventoryResponse;
import com.example.catalog.service.repository.ProductRepository;
import com.example.catalog.service.rest.InventoryServiceClient;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static java.util.Objects.requireNonNull;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final InventoryServiceClient inventoryServiceClient;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByCode(String code) {
        Optional<Product> productOptional = productRepository.findByCode(code);
        if (productOptional.isPresent()) {
            log.info("Fetching inventory level for product_code: " + code);
            ProductInventoryResponse itemInventory = inventoryServiceClient.getProductInventory(code);
            int quantity = requireNonNull(itemInventory).getAvailableQuantity();
            log.info("Available quantity: " + quantity);
            productOptional.get().setInStock(quantity > 0);
        }
        return productOptional;
    }
}
