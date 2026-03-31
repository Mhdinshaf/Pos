package com.pos.repository;

import com.pos.model.Employee;

import java.util.List;

public interface EmployeeRepository {
    List<Employee> findAll();
    Employee findById(int id);
    List<Employee> findByName(String name);
    boolean save(Employee employee);
    boolean update(Employee employee);
    boolean delete(int id);
}
