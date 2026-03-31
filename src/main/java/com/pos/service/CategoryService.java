package com.pos.service;

import com.pos.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(int id);
    boolean addCategory(String categoryName);
    boolean updateCategory(int categoryId, String categoryName);
    boolean deleteCategory(int id);
}
