package com.pos.service.impl;

import com.pos.model.Product;
import com.pos.repository.ProductRepository;
import com.pos.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStock(threshold);
    }

    @Override
    public boolean addProduct(Product product) {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() == null || product.getPrice().doubleValue() < 0) {
            return false;
        }
        if (product.getQuantity() < 0) {
            return false;
        }
        return productRepository.save(product);
    }

    @Override
    public boolean updateProduct(Product product) {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() == null || product.getPrice().doubleValue() < 0) {
            return false;
        }
        if (product.getQuantity() < 0) {
            return false;
        }
        return productRepository.update(product);
    }

    @Override
    public boolean updateStock(int productId, int quantity) {
        if (quantity < 0) {
            return false;
        }
        return productRepository.updateStock(productId, quantity);
    }

    @Override
    public boolean deleteProduct(int id) {
        return productRepository.delete(id);
    }
}
