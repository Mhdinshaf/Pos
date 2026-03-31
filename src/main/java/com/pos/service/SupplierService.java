package com.pos.service;

import com.pos.model.Supplier;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Supplier getSupplierById(int id);
    List<Supplier> searchSuppliersByName(String name);
    boolean addSupplier(String name, String email, String phone);
    boolean updateSupplier(Supplier supplier);
    boolean deleteSupplier(int id);
}
