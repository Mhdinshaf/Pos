package com.pos.service;

import com.pos.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    List<Employee> searchEmployeesByName(String name);
    boolean addEmployee(String name, String role, String email, String phone);
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(int id);
}
