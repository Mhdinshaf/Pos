package com.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    private int productId;
    private String name;
    private Integer categoryId;
    private BigDecimal price;
    private int quantity;
    private Integer supplierId;

    public Product(String name, Integer categoryId, BigDecimal price, int quantity, Integer supplierId) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }
}
