package com.pos.repository;

import com.pos.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
    Category findById(int id);
    boolean save(Category category);
    boolean update(Category category);
    boolean delete(int id);
}
