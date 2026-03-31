package com.pos.repository;

import com.pos.model.Order;
import com.pos.model.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository {
    int saveOrder(Order order);
    boolean saveOrderItem(OrderItem orderItem);
    List<Order> findAllOrders();
    Order findOrderById(int id);
    List<OrderItem> findOrderItemsByOrderId(int orderId);
    List<Order> findOrdersByDateRange(LocalDate startDate, LocalDate endDate);
}
