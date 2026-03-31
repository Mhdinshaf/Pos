package com.pos.service;

import com.pos.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    List<Product> searchProductsByName(String name);
    List<Product> getLowStockProducts(int threshold);
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean updateStock(int productId, int quantity);
    boolean deleteProduct(int id);
}
