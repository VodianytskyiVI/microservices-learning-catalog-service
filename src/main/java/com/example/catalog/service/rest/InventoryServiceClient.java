package com.example.catalog.service.rest;

import com.example.catalog.service.model.ProductInventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventoryService", url = "localhost:8282", fallback = InventoryServiceClient.Fallback.class)
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/{productCode}")
    ProductInventoryResponse getProductInventory(@PathVariable("productCode") String productCode);

    @Slf4j
    @Component
    class Fallback implements InventoryServiceClient {

        @Override
        public ProductInventoryResponse getProductInventory(@PathVariable("productCode") String productCode) {
            log.info("Returning default ProductInventoryByCode for productCode: " + productCode);
            ProductInventoryResponse response = new ProductInventoryResponse();
            response.setProductCode(productCode);
            response.setAvailableQuantity(50);
            return response;
        }
    }
}