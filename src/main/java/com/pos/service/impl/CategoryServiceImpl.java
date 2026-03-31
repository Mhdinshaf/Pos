package com.pos.service.impl;

import com.pos.model.Category;
import com.pos.repository.CategoryRepository;
import com.pos.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public boolean addCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        Category category = new Category(categoryName.trim());
        return categoryRepository.save(category);
    }

    @Override
    public boolean updateCategory(int categoryId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        Category category = new Category(categoryId, categoryName.trim());
        return categoryRepository.update(category);
    }

    @Override
    public boolean deleteCategory(int id) {
        return categoryRepository.delete(id);
    }
}
