package com.pos.service.impl;

import com.pos.model.Employee;
import com.pos.repository.EmployeeRepository;
import com.pos.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> searchEmployeesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return employeeRepository.findAll();
        }
        return employeeRepository.findByName(name);
    }

    @Override
    public boolean addEmployee(String name, String role, String email, String phone) {
        if (!isValidInput(name, role, phone)) {
            return false;
        }
        Employee employee = new Employee(name, role, email, phone);
        return employeeRepository.save(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        if (employee == null) {
            return false;
        }
        if (!isValidInput(employee.getName(), employee.getRole(), employee.getPhone())) {
            return false;
        }
        return employeeRepository.update(employee);
    }

    @Override
    public boolean deleteEmployee(int id) {
        return employeeRepository.delete(id);
    }

    private boolean isValidInput(String name, String role, String phone) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
