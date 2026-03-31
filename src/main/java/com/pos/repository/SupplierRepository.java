package com.pos.repository;

import com.pos.model.Supplier;

import java.util.List;

public interface SupplierRepository {
    List<Supplier> findAll();
    Supplier findById(int id);
    List<Supplier> findByName(String name);
    boolean save(Supplier supplier);
    boolean update(Supplier supplier);
    boolean delete(int id);
}
