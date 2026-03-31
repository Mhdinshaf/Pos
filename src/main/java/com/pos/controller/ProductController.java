package com.pos.controller;

import com.pos.model.Category;
import com.pos.model.Product;
import com.pos.model.Supplier;
import com.pos.repository.impl.CategoryRepositoryImpl;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.repository.impl.SupplierRepositoryImpl;
import com.pos.service.CategoryService;
import com.pos.service.ProductService;
import com.pos.service.SupplierService;
import com.pos.service.impl.CategoryServiceImpl;
import com.pos.service.impl.ProductServiceImpl;
import com.pos.service.impl.SupplierServiceImpl;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductController {

    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, Integer> colProductId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colSupplier;
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<String> cmbSupplier;
    @FXML private TextField txtSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnBackToDashboard;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredList;
    private Product selectedProduct;

    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> supplierMap = new HashMap<>();
    private Map<Integer, String> categoryIdToName = new HashMap<>();
    private Map<Integer, String> supplierIdToName = new HashMap<>();

    public ProductController() throws SQLException, ClassNotFoundException {
        this.productService = new ProductServiceImpl(new ProductRepositoryImpl());
        this.categoryService = new CategoryServiceImpl(new CategoryRepositoryImpl());
        this.supplierService = new SupplierServiceImpl(new SupplierRepositoryImpl());
    }

    @FXML
    public void initialize() {
        loadCategories();
        loadSuppliers();
        setupTableColumns();
        loadProducts();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
    }

    private void loadCategories() {
        categoryMap.clear();
        categoryIdToName.clear();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();
        for (Category category : categoryService.getAllCategories()) {
            categoryMap.put(category.getCategoryName(), category.getCategoryId());
            categoryIdToName.put(category.getCategoryId(), category.getCategoryName());
            categoryNames.add(category.getCategoryName());
        }
        cmbCategory.setItems(categoryNames);
    }

    private void loadSuppliers() {
        supplierMap.clear();
        supplierIdToName.clear();
        ObservableList<String> supplierNames = FXCollections.observableArrayList();
        for (Supplier supplier : supplierService.getAllSuppliers()) {
            supplierMap.put(supplier.getName(), supplier.getSupplierId());
            supplierIdToName.put(supplier.getSupplierId(), supplier.getName());
            supplierNames.add(supplier.getName());
        }
        cmbSupplier.setItems(supplierNames);
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
        colSupplier.setCellValueFactory(cellData -> {
            Integer supplierId = cellData.getValue().getSupplierId();
            String supplierName = supplierId != null ? supplierIdToName.getOrDefault(supplierId, "N/A") : "N/A";
            return new SimpleStringProperty(supplierName);
        });
    }

    private void loadProducts() {
        productList = FXCollections.observableArrayList(productService.getAllProducts());
        filteredList = new FilteredList<>(productList, p -> true);
        tblProducts.setItems(filteredList);
    }

    private void setupTableSelection() {
        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                txtName.setText(newSelection.getName());
                txtPrice.setText(newSelection.getPrice().toString());
                txtQuantity.setText(String.valueOf(newSelection.getQuantity()));
                if (newSelection.getCategoryId() != null) {
                    cmbCategory.setValue(categoryIdToName.get(newSelection.getCategoryId()));
                } else {
                    cmbCategory.setValue(null);
                }
                if (newSelection.getSupplierId() != null) {
                    cmbSupplier.setValue(supplierIdToName.get(newSelection.getSupplierId()));
                } else {
                    cmbSupplier.setValue(null);
                }
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
        btnAdd.setOnAction(event -> handleAdd());
        btnUpdate.setOnAction(event -> handleUpdate());
        btnDelete.setOnAction(event -> handleDelete());
        btnClear.setOnAction(event -> handleClear());
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

    private void handleAdd() {
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        String quantityText = txtQuantity.getText().trim();
        String categoryName = cmbCategory.getValue();
        String supplierName = cmbSupplier.getValue();

        if (!validateInputs(name, priceText, quantityText)) return;

        try {
            BigDecimal price = new BigDecimal(priceText);
            int quantity = Integer.parseInt(quantityText);
            Integer categoryId = categoryName != null ? categoryMap.get(categoryName) : null;
            Integer supplierId = supplierName != null ? supplierMap.get(supplierName) : null;

            Product product = new Product(name, categoryId, price, quantity, supplierId);
            boolean success = productService.addProduct(product);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully.");
                loadProducts();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price or quantity format.");
        }
    }

    private void handleUpdate() {
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to update.");
            return;
        }

        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        String quantityText = txtQuantity.getText().trim();
        String categoryName = cmbCategory.getValue();
        String supplierName = cmbSupplier.getValue();

        if (!validateInputs(name, priceText, quantityText)) return;

        try {
            selectedProduct.setName(name);
            selectedProduct.setCategoryId(categoryName != null ? categoryMap.get(categoryName) : null);
            selectedProduct.setPrice(new BigDecimal(priceText));
            selectedProduct.setQuantity(Integer.parseInt(quantityText));
            selectedProduct.setSupplierId(supplierName != null ? supplierMap.get(supplierName) : null);

            boolean success = productService.updateProduct(selectedProduct);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully.");
                loadProducts();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price or quantity format.");
        }
    }

    private void handleDelete() {
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete product: " + selectedProduct.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = productService.deleteProduct(selectedProduct.getProductId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully.");
                loadProducts();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product.");
            }
        }
    }

    private void handleClear() {
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtSearch.clear();
        cmbCategory.setValue(null);
        cmbSupplier.setValue(null);
        selectedProduct = null;
        tblProducts.getSelectionModel().clearSelection();
    }

    private boolean validateInputs(String name, String price, String quantity) {
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Product name is required.");
            return false;
        }
        if (price.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price is required.");
            return false;
        }
        if (quantity.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Quantity is required.");
            return false;
        }
        try {
            new BigDecimal(price);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid price format.");
            return false;
        }
        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid quantity format.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
