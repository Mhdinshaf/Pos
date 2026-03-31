package com.pos.repository;

import com.pos.model.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(int id);
    List<Product> findByName(String name);
    List<Product> findLowStock(int threshold);
    boolean save(Product product);
    boolean update(Product product);
    boolean updateStock(int productId, int quantity);
    boolean delete(int id);
}
