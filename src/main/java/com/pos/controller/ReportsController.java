package com.pos.controller;

import com.pos.model.Category;
import com.pos.model.Order;
import com.pos.model.Product;
import com.pos.repository.impl.CategoryRepositoryImpl;
import com.pos.repository.impl.OrderRepositoryImpl;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.service.CategoryService;
import com.pos.service.OrderService;
import com.pos.service.ProductService;
import com.pos.service.impl.CategoryServiceImpl;
import com.pos.service.impl.OrderServiceImpl;
import com.pos.service.impl.ProductServiceImpl;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsController {

    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private Label lblTotalSales;
    @FXML private Label lblTotalOrders;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStockItems;
    @FXML private Label lblTotalStockValue;
    @FXML private TableView<Order> tblSales;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colOrderDate;
    @FXML private TableColumn<Order, String> colTotalAmount;
    @FXML private TableView<Product> tblInventory;
    @FXML private TableColumn<Product, Integer> colProductId;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colStockValue;
    @FXML private Button btnGenerateReport;
    @FXML private Button btnBackToDashboard;

    private final OrderService orderService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private Map<Integer, String> categoryIdToName = new HashMap<>();

    public ReportsController() throws SQLException, ClassNotFoundException {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.orderService = new OrderServiceImpl(new OrderRepositoryImpl(), productRepository);
        this.productService = new ProductServiceImpl(productRepository);
        this.categoryService = new CategoryServiceImpl(new CategoryRepositoryImpl());
    }

    @FXML
    public void initialize() {
        loadCategoryNames();
        setupDatePickers();
        setupSalesTable();
        setupInventoryTable();
        setupButtonActions();
        loadInventorySummary();
    }

    private void loadCategoryNames() {
        for (Category category : categoryService.getAllCategories()) {
            categoryIdToName.put(category.getCategoryId(), category.getCategoryName());
        }
    }

    private void setupDatePickers() {
        dpStartDate.setValue(LocalDate.now().minusMonths(1));
        dpEndDate.setValue(LocalDate.now());
    }

    private void setupSalesTable() {
        colOrderId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        colOrderDate.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(cellData.getValue().getOrderDate().format(formatter));
        });
        colTotalAmount.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("Rs. %.2f", cellData.getValue().getTotalAmount())));
    }

    private void setupInventoryTable() {
        colProductId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductId()).asObject());
        colProductName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            String categoryName = categoryId != null ? categoryIdToName.getOrDefault(categoryId, "N/A") : "N/A";
            return new SimpleStringProperty(categoryName);
        });
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("Rs. %.2f", cellData.getValue().getPrice())));
        colQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colStockValue.setCellValueFactory(cellData -> {
            BigDecimal price = cellData.getValue().getPrice();
            int qty = cellData.getValue().getQuantity();
            double value = price.doubleValue() * qty;
            return new SimpleStringProperty(String.format("Rs. %.2f", value));
        });
    }

    private void setupButtonActions() {
        btnGenerateReport.setOnAction(event -> generateSalesReport());
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBackToDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clothify Store - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
        }
    }

    private void generateSalesReport() {
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();

        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select both start and end dates.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Start date cannot be after end date.");
            return;
        }

        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        ObservableList<Order> orderList = FXCollections.observableArrayList(orders);
        tblSales.setItems(orderList);

        // Calculate totals
        double totalSales = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        int totalOrders = orders.size();

        lblTotalSales.setText(String.format("Rs. %.2f", totalSales));
        lblTotalOrders.setText(String.valueOf(totalOrders));
    }

    private void loadInventorySummary() {
        List<Product> products = productService.getAllProducts();
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        tblInventory.setItems(productList);

        // Calculate inventory stats
        int totalProducts = products.size();
        int lowStockItems = (int) products.stream().filter(p -> p.getQuantity() < 5).count();
        double totalStockValue = products.stream()
                .mapToDouble(p -> p.getPrice().doubleValue() * p.getQuantity())
                .sum();

        lblTotalProducts.setText(String.valueOf(totalProducts));
        lblLowStockItems.setText(String.valueOf(lowStockItems));
        lblTotalStockValue.setText(String.format("Rs. %.2f", totalStockValue));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
