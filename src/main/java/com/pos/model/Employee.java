package com.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    private int employeeId;
    private String name;
    private String role;
    private String email;
    private String phone;

    public Employee(String name, String role, String email, String phone) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }
}
