package com.example.catalog.service.service;

import com.example.catalog.service.model.Product;
import com.example.catalog.service.model.ProductInventoryResponse;
import com.example.catalog.service.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import static java.util.Objects.requireNonNull;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final RestTemplate restTemplate;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByCode(String code) {
        Optional<Product> productOptional = productRepository.findByCode(code);
        if (productOptional.isPresent()) {
            log.info("Fetching inventory level for product_code: " + code);
            ResponseEntity<ProductInventoryResponse> itemResponseEntity =
                    restTemplate.getForEntity("http://inventory-service/api/inventory/{code}",
                            ProductInventoryResponse.class,
                            code);
            if (itemResponseEntity.getStatusCode() == HttpStatus.OK) {
                int quantity = requireNonNull(itemResponseEntity.getBody()).getAvailableQuantity();
                log.info("Available quantity: " + quantity);
                productOptional.get().setInStock(quantity > 0);
            } else {
                log.error("Unable to get inventory level for product_code: " + code +
                        ", StatusCode: " + itemResponseEntity.getStatusCode());
            }
        }
        return productOptional;
    }
}
