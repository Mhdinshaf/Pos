package com.pos.controller;

import com.pos.model.Order;
import com.pos.repository.impl.*;
import com.pos.service.*;
import com.pos.service.impl.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    // Summary Cards
    @FXML
    private Label lblTotalProducts;

    @FXML
    private Label lblTotalSuppliers;

    @FXML
    private Label lblTotalEmployees;

    @FXML
    private Label lblTodaySales;

    // Navigation Buttons
    @FXML
    private Button btnProducts;

    @FXML
    private Button btnCategories;

    @FXML
    private Button btnSuppliers;

    @FXML
    private Button btnEmployees;

    @FXML
    private Button btnInventory;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnLogout;

    private final ProductService productService;
    private final SupplierService supplierService;
    private final EmployeeService employeeService;
    private final OrderService orderService;

    public DashboardController() throws SQLException, ClassNotFoundException {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.productService = new ProductServiceImpl(productRepository);
        this.supplierService = new SupplierServiceImpl(new SupplierRepositoryImpl());
        this.employeeService = new EmployeeServiceImpl(new EmployeeRepositoryImpl());
        this.orderService = new OrderServiceImpl(new OrderRepositoryImpl(), productRepository);
    }

    @FXML
    public void initialize() {
        loadSummaryData();
        setupNavigationButtons();
    }

    private void loadSummaryData() {
        // Total Products
        int totalProducts = productService.getAllProducts().size();
        lblTotalProducts.setText(String.valueOf(totalProducts));

        // Total Suppliers
        int totalSuppliers = supplierService.getAllSuppliers().size();
        lblTotalSuppliers.setText(String.valueOf(totalSuppliers));

        // Total Employees
        int totalEmployees = employeeService.getAllEmployees().size();
        lblTotalEmployees.setText(String.valueOf(totalEmployees));

        // Today's Sales
        LocalDate today = LocalDate.now();
        List<Order> todayOrders = orderService.getOrdersByDateRange(today, today);
        double todaySales = todayOrders.stream().mapToDouble(Order::getTotalAmount).sum();
        lblTodaySales.setText(String.format("Rs. %.2f", todaySales));
    }

    private void setupNavigationButtons() {
        btnProducts.setOnAction(event -> navigateTo("/view/Product.fxml", "Product Management"));
        btnCategories.setOnAction(event -> navigateTo("/view/Category.fxml", "Category Management"));
        btnSuppliers.setOnAction(event -> navigateTo("/view/Supplier.fxml", "Supplier Management"));
        btnEmployees.setOnAction(event -> navigateTo("/view/Employee.fxml", "Employee Management"));
        btnInventory.setOnAction(event -> navigateTo("/view/Inventory.fxml", "Inventory Management"));
        btnOrders.setOnAction(event -> navigateTo("/view/Order.fxml", "Place Order"));
        btnReports.setOnAction(event -> navigateTo("/view/Reports.fxml", "Reports"));
        btnLogout.setOnAction(event -> handleLogout());
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnProducts.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clothify Store - " + title);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load " + title + " screen.");
        }
    }

    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Logout");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Clothify Store - Login");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
