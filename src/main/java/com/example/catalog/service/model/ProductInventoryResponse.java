package com.example.catalog.service.model;

import lombok.Data;

@Data
public class ProductInventoryResponse {
    private String productCode;
    private int availableQuantity;
}
