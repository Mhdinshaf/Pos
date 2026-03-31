package com.pos.service;

import com.pos.model.Order;
import com.pos.model.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    int placeOrder(int userId, List<OrderItem> orderItems);
    Order getOrderById(int id);
    List<Order> getAllOrders();
    List<OrderItem> getOrderItems(int orderId);
    List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate);
}
