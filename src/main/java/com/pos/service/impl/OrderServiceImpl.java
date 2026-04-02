package com.pos.service.impl;

import com.pos.model.Order;
import com.pos.model.OrderItem;
import com.pos.model.Product;
import com.pos.repository.OrderRepository;
import com.pos.repository.ProductRepository;
import com.pos.service.OrderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public int placeOrder(int userId, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return -1;
        }

        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId());
            if (product == null) {
                return -1;
            }
            if (product.getQuantity() < item.getQuantity()) {
                return -1;
            }
        }


        double totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getUnitPrice() * item.getQuantity();
        }


        Order order = new Order(LocalDateTime.now(), totalAmount, userId);
        int orderId = orderRepository.saveOrder(order);
        if (orderId == -1) {
            return -1;
        }


        for (OrderItem item : orderItems) {
            item.setOrderId(orderId);
            boolean itemSaved = orderRepository.saveOrderItem(item);
            if (!itemSaved) {
                return -1;
            }


            Product product = productRepository.findById(item.getProductId());
            int newQuantity = product.getQuantity() - item.getQuantity();
            productRepository.updateStock(item.getProductId(), newQuantity);
        }

        return orderId;
    }

    @Override
    public Order getOrderById(int id) {
        return orderRepository.findOrderById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        return orderRepository.findOrderItemsByOrderId(orderId);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return orderRepository.findAllOrders();
        }
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }
}
