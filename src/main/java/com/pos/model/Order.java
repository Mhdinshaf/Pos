package com.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private int orderId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private int userId;

    public Order(LocalDateTime orderDate, double totalAmount, int userId) {
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    public Order(double totalAmount, int userId) {
        this.orderDate = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.userId = userId;
    }
}
