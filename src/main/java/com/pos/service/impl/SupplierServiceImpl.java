package com.pos.service.impl;

import com.pos.model.Supplier;
import com.pos.repository.SupplierRepository;
import com.pos.service.SupplierService;

import java.util.List;

public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> searchSuppliersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return supplierRepository.findAll();
        }
        return supplierRepository.findByName(name);
    }

    @Override
    public boolean addSupplier(String name, String email, String phone) {
        if (!isValidInput(name, email, phone)) {
            return false;
        }
        Supplier supplier = new Supplier(name, email, phone);
        return supplierRepository.save(supplier);
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        if (supplier == null) {
            return false;
        }
        if (!isValidInput(supplier.getName(), supplier.getEmail(), supplier.getPhone())) {
            return false;
        }
        return supplierRepository.update(supplier);
    }

    @Override
    public boolean deleteSupplier(int id) {
        return supplierRepository.delete(id);
    }

    private boolean isValidInput(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        if (email == null || !email.contains("@")) {
            return false;
        }
        return true;
    }
}
