package com.pos.controller;

import com.pos.model.Product;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.service.ProductService;
import com.pos.service.impl.ProductServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class InventoryController {

    @FXML
    private TableView<Product> tblProducts;

    @FXML
    private TableColumn<Product, Integer> colProductId;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TableColumn<Product, String> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtNewQuantity;

    @FXML
    private Button btnUpdateStock;

    @FXML
    private Label lblSelectedProduct;

    @FXML
    private Label lblLowStockCount;

    private final ProductService productService;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredList;
    private Product selectedProduct;

    private static final int LOW_STOCK_THRESHOLD = 5;

    public InventoryController() throws SQLException, ClassNotFoundException {
        this.productService = new ProductServiceImpl(new ProductRepositoryImpl());
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupRowFactory();
        loadProducts();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
        checkLowStockWarning();
    }

    private void setupTableColumns() {
        colProductId.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getProductId()).asObject());
        colName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            return new SimpleStringProperty(categoryId != null ? "Category " + categoryId : "N/A");
        });
        colPrice.setCellValueFactory(cellData -> 
            new SimpleStringProperty("Rs. " + cellData.getValue().getPrice().toString()));
        colQuantity.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
    }

    private void setupRowFactory() {
        tblProducts.setRowFactory(tv -> new TableRow<Product>() {
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

    private void loadProducts() {
        productList = FXCollections.observableArrayList(productService.getAllProducts());
        filteredList = new FilteredList<>(productList, p -> true);
        tblProducts.setItems(filteredList);
        updateLowStockCount();
    }

    private void setupTableSelection() {
        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                lblSelectedProduct.setText("Selected: " + newSelection.getName() + " (Current Qty: " + newSelection.getQuantity() + ")");
                txtNewQuantity.setText(String.valueOf(newSelection.getQuantity()));
            } else {
                selectedProduct = null;
                lblSelectedProduct.setText("No product selected");
                txtNewQuantity.clear();
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupButtonActions() {
        btnUpdateStock.setOnAction(event -> handleUpdateStock());
    }

    private void handleUpdateStock() {
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to update stock.");
            return;
        }

        String quantityText = txtNewQuantity.getText().trim();
        if (quantityText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a quantity.");
            return;
        }

        int newQuantity;
        try {
            newQuantity = Integer.parseInt(quantityText);
            if (newQuantity < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid number.");
            return;
        }

        boolean success = productService.updateStock(selectedProduct.getProductId(), newQuantity);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Stock updated successfully.");
            loadProducts();
            clearSelection();
            checkLowStockWarning();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update stock.");
        }
    }

    private void clearSelection() {
        selectedProduct = null;
        lblSelectedProduct.setText("No product selected");
        txtNewQuantity.clear();
        tblProducts.getSelectionModel().clearSelection();
    }

    private void updateLowStockCount() {
        List<Product> lowStockProducts = productService.getLowStockProducts(LOW_STOCK_THRESHOLD);
        lblLowStockCount.setText("Low Stock Items: " + lowStockProducts.size());
    }

    private void checkLowStockWarning() {
        List<Product> lowStockProducts = productService.getLowStockProducts(LOW_STOCK_THRESHOLD);
        if (!lowStockProducts.isEmpty()) {
            StringBuilder message = new StringBuilder("The following products are low on stock:\n\n");
            for (Product product : lowStockProducts) {
                message.append("• ").append(product.getName())
                       .append(" (Qty: ").append(product.getQuantity()).append(")\n");
            }
            showAlert(Alert.AlertType.WARNING, "Low Stock Warning", message.toString());
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
