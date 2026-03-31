package com.pos.controller;

import com.pos.model.Order;
import com.pos.model.Product;
import com.pos.repository.impl.OrderRepositoryImpl;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.service.OrderService;
import com.pos.service.ProductService;
import com.pos.service.impl.OrderServiceImpl;
import com.pos.service.impl.ProductServiceImpl;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsController {

    // Sales Report Components
    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private Button btnGenerateReport;

    @FXML
    private TableView<Order> tblOrders;

    @FXML
    private TableColumn<Order, Integer> colOrderId;

    @FXML
    private TableColumn<Order, String> colOrderDate;

    @FXML
    private TableColumn<Order, Double> colTotalAmount;

    @FXML
    private Label lblTotalOrders;

    @FXML
    private Label lblTotalSales;

    // Inventory Report Components
    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Product> tblInventory;

    @FXML
    private TableColumn<Product, Integer> colProductId;

    @FXML
    private TableColumn<Product, String> colProductName;

    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TableColumn<Product, String> colPrice;

    @FXML
    private Label lblLowStockCount;

    private final OrderService orderService;
    private final ProductService productService;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredProductList;

    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportsController() throws SQLException, ClassNotFoundException {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.orderService = new OrderServiceImpl(new OrderRepositoryImpl(), productRepository);
        this.productService = new ProductServiceImpl(productRepository);
    }

    @FXML
    public void initialize() {
        setupDatePickers();
        setupSalesTableColumns();
        setupInventoryTableColumns();
        setupInventoryRowFactory();
        loadInventory();
        setupSearch();
        setupButtonActions();
    }

    private void setupDatePickers() {
        dpStartDate.setValue(LocalDate.now().minusMonths(1));
        dpEndDate.setValue(LocalDate.now());
    }

    private void setupSalesTableColumns() {
        colOrderId.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getOrderId()).asObject());
        colOrderDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getOrderDate() != null) {
                return new SimpleStringProperty(cellData.getValue().getOrderDate().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("N/A");
        });
        colTotalAmount.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getTotalAmount()).asObject());
    }

    private void setupInventoryTableColumns() {
        colProductId.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getProductId()).asObject());
        colProductName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryId != null ? "Category " + categoryId : "N/A");
        });
        colQuantity.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(cellData -> 
            new SimpleStringProperty("Rs. " + cellData.getValue().getPrice().toString()));
    }

    private void setupInventoryRowFactory() {
        tblInventory.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setStyle("");
                } else if (product.getQuantity() < LOW_STOCK_THRESHOLD) {
                    setStyle("-fx-background-color: #8B0000;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void loadInventory() {
        productList = FXCollections.observableArrayList(productService.getAllProducts());
        filteredProductList = new FilteredList<>(productList, p -> true);
        tblInventory.setItems(filteredProductList);
        updateLowStockCount();
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProductList.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupButtonActions() {
        btnGenerateReport.setOnAction(event -> handleGenerateReport());
    }

    private void handleGenerateReport() {
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
        tblOrders.setItems(FXCollections.observableArrayList(orders));

        // Calculate summary
        int totalOrders = orders.size();
        double totalSales = orders.stream().mapToDouble(Order::getTotalAmount).sum();

        lblTotalOrders.setText(String.valueOf(totalOrders));
        lblTotalSales.setText(String.format("Rs. %.2f", totalSales));
    }

    private void updateLowStockCount() {
        List<Product> lowStockProducts = productService.getLowStockProducts(LOW_STOCK_THRESHOLD);
        lblLowStockCount.setText(String.valueOf(lowStockProducts.size()));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
