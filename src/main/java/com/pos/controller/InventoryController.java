package com.pos.controller;

import com.pos.model.Category;
import com.pos.model.Product;
import com.pos.repository.impl.CategoryRepositoryImpl;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.service.CategoryService;
import com.pos.service.ProductService;
import com.pos.service.impl.CategoryServiceImpl;
import com.pos.service.impl.ProductServiceImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryController {

    @FXML private TableView<Product> tblInventory;
    @FXML private TableColumn<Product, Integer> colProductId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private TextField txtSearch;
    @FXML private TextField txtNewQuantity;
    @FXML private Label lblSelectedProduct;
    @FXML private Label lblCurrentStock;
    @FXML private Label lblLowStockCount;
    @FXML private HBox hboxLowStockWarning;
    @FXML private Button btnUpdateStock;
    @FXML private Button btnShowLowStock;
    @FXML private Button btnShowAll;
    @FXML private Button btnBackToDashboard;

    private final ProductService productService;
    private final CategoryService categoryService;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredList;
    private Product selectedProduct;
    private Map<Integer, String> categoryIdToName = new HashMap<>();

    public InventoryController() throws SQLException, ClassNotFoundException {
        this.productService = new ProductServiceImpl(new ProductRepositoryImpl());
        this.categoryService = new CategoryServiceImpl(new CategoryRepositoryImpl());
    }

    @FXML
    public void initialize() {
        loadCategoryNames();
        setupTableColumns();
        loadProducts();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
        updateLowStockWarning();
    }

    private void loadCategoryNames() {
        for (Category category : categoryService.getAllCategories()) {
            categoryIdToName.put(category.getCategoryId(), category.getCategoryName());
        }
    }

    private void setupTableColumns() {
        colProductId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> {
            Integer categoryId = cellData.getValue().getCategoryId();
            String categoryName = categoryId != null ? categoryIdToName.getOrDefault(categoryId, "N/A") : "N/A";
            return new SimpleStringProperty(categoryName);
        });
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("Rs. %.2f", cellData.getValue().getPrice())));
        colQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colStatus.setCellValueFactory(cellData -> {
            int qty = cellData.getValue().getQuantity();
            if (qty < 5) return new SimpleStringProperty("LOW");
            else if (qty < 20) return new SimpleStringProperty("MEDIUM");
            else return new SimpleStringProperty("OK");
        });

        // Row factory for highlighting low stock
        tblInventory.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null || empty) {
                    setStyle("");
                } else if (product.getQuantity() < 5) {
                    setStyle("-fx-background-color: #ff6b6b;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void loadProducts() {
        productList = FXCollections.observableArrayList(productService.getAllProducts());
        filteredList = new FilteredList<>(productList, p -> true);
        tblInventory.setItems(filteredList);
    }

    private void setupTableSelection() {
        tblInventory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                lblSelectedProduct.setText(newSelection.getName());
                lblCurrentStock.setText(String.valueOf(newSelection.getQuantity()));
                txtNewQuantity.clear();
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return product.getName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void setupButtonActions() {
        btnUpdateStock.setOnAction(event -> handleUpdateStock());
        btnShowLowStock.setOnAction(event -> showLowStockOnly());
        btnShowAll.setOnAction(event -> showAllProducts());
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

    private void handleUpdateStock() {
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to update.");
            return;
        }

        String newQtyText = txtNewQuantity.getText().trim();
        if (newQtyText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "New quantity is required.");
            return;
        }

        try {
            int newQuantity = Integer.parseInt(newQtyText);
            if (newQuantity < 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Quantity cannot be negative.");
                return;
            }

            boolean success = productService.updateStock(selectedProduct.getProductId(), newQuantity);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Stock updated successfully.");
                loadProducts();
                updateLowStockWarning();
                lblSelectedProduct.setText("No product selected");
                lblCurrentStock.setText("-");
                txtNewQuantity.clear();
                selectedProduct = null;
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update stock.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid quantity format.");
        }
    }

    private void showLowStockOnly() {
        filteredList.setPredicate(product -> product.getQuantity() < 5);
    }

    private void showAllProducts() {
        filteredList.setPredicate(product -> true);
        txtSearch.clear();
    }

    private void updateLowStockWarning() {
        List<Product> lowStockProducts = productService.getLowStockProducts(5);
        int count = lowStockProducts.size();
        if (count > 0) {
            hboxLowStockWarning.setVisible(true);
            lblLowStockCount.setText(count + " products have low stock (less than 5 items)");
        } else {
            hboxLowStockWarning.setVisible(false);
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
