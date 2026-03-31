package com.pos.controller;

import com.pos.model.OrderItem;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderController {

    @FXML private TextField txtProductSearch;
    @FXML private ComboBox<Product> cmbProduct;
    @FXML private TextField txtQuantity;
    @FXML private Label lblProductPrice;
    @FXML private Label lblAvailableStock;
    @FXML private Label lblItemCount;
    @FXML private Label lblTotalAmount;
    @FXML private TableView<CartItem> tblCart;
    @FXML private TableColumn<CartItem, String> colProductName;
    @FXML private TableColumn<CartItem, String> colUnitPrice;
    @FXML private TableColumn<CartItem, Integer> colCartQuantity;
    @FXML private TableColumn<CartItem, String> colSubtotal;
    @FXML private Button btnAddToCart;
    @FXML private Button btnRemoveFromCart;
    @FXML private Button btnClearCart;
    @FXML private Button btnPlaceOrder;
    @FXML private Button btnBackToDashboard;

    private final ProductService productService;
    private final OrderService orderService;
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    public OrderController() throws SQLException, ClassNotFoundException {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.productService = new ProductServiceImpl(productRepository);
        this.orderService = new OrderServiceImpl(new OrderRepositoryImpl(), productRepository);
    }

    @FXML
    public void initialize() {
        loadProducts();
        setupProductComboBox();
        setupProductSearch();
        setupCartTable();
        setupProductSelection();
        setupButtonActions();
        updateTotals();
    }

    private void loadProducts() {
        allProducts.clear();
        for (Product product : productService.getAllProducts()) {
            if (product.getQuantity() > 0) {
                allProducts.add(product);
            }
        }
        cmbProduct.setItems(FXCollections.observableArrayList(allProducts));
    }

    private void setupProductComboBox() {
        // Set up StringConverter to display product name in ComboBox
        cmbProduct.setConverter(new javafx.util.StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                return product == null ? "" : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return allProducts.stream()
                        .filter(p -> p.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Set cell factory for dropdown display
        cmbProduct.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setText(product.getName() + " - Rs. " + product.getPrice() + " (Stock: " + product.getQuantity() + ")");
                }
                setStyle("-fx-text-fill: #ffffff; -fx-background-color: #2d3250;");
            }
        });
    }

    private void setupProductSearch() {
        txtProductSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                cmbProduct.setItems(FXCollections.observableArrayList(allProducts));
            } else {
                ObservableList<Product> filtered = FXCollections.observableArrayList();
                for (Product product : allProducts) {
                    if (product.getName().toLowerCase().contains(newValue.toLowerCase())) {
                        filtered.add(product);
                    }
                }
                cmbProduct.setItems(filtered);
                if (!filtered.isEmpty()) {
                    cmbProduct.show();
                }
            }
        });
    }

    private void setupCartTable() {
        colProductName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        colUnitPrice.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("Rs. %.2f", cellData.getValue().getUnitPrice())));
        colCartQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colSubtotal.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("Rs. %.2f", cellData.getValue().getSubtotal())));
        tblCart.setItems(cartItems);
    }

    private void setupProductSelection() {
        cmbProduct.setOnAction(event -> {
            Product selectedProduct = cmbProduct.getValue();
            if (selectedProduct != null) {
                lblProductPrice.setText(String.format("Rs. %.2f", selectedProduct.getPrice()));
                lblAvailableStock.setText(String.valueOf(selectedProduct.getQuantity()));
            } else {
                lblProductPrice.setText("Rs. 0.00");
                lblAvailableStock.setText("0");
            }
        });
    }

    private void setupButtonActions() {
        btnAddToCart.setOnAction(event -> handleAddToCart());
        btnRemoveFromCart.setOnAction(event -> handleRemoveFromCart());
        btnClearCart.setOnAction(event -> handleClearCart());
        btnPlaceOrder.setOnAction(event -> handlePlaceOrder());
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

    private void handleAddToCart() {
        Product selectedProduct = cmbProduct.getValue();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product.");
            return;
        }

        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter quantity.");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Quantity must be greater than 0.");
                return;
            }

            if (quantity > selectedProduct.getQuantity()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Not enough stock. Available: " + selectedProduct.getQuantity());
                return;
            }

            // Check if product already in cart
            for (CartItem item : cartItems) {
                if (item.getProductId() == selectedProduct.getProductId()) {
                    int newQty = item.getQuantity() + quantity;
                    if (newQty > selectedProduct.getQuantity()) {
                        showAlert(Alert.AlertType.WARNING, "Warning", "Total quantity exceeds available stock.");
                        return;
                    }
                    item.setQuantity(newQty);
                    tblCart.refresh();
                    updateTotals();
                    txtQuantity.clear();
                    return;
                }
            }

            // Add new item
            CartItem cartItem = new CartItem(selectedProduct.getProductId(), selectedProduct.getName(), selectedProduct.getPrice().doubleValue(), quantity);
            cartItems.add(cartItem);
            updateTotals();
            txtQuantity.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Invalid quantity format.");
        }
    }

    private void handleRemoveFromCart() {
        CartItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to remove.");
            return;
        }
        cartItems.remove(selected);
        updateTotals();
    }

    private void handleClearCart() {
        cartItems.clear();
        updateTotals();
    }

    private void handlePlaceOrder() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Cart is empty. Please add items.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            orderItems.add(new OrderItem(item.getProductId(), item.getQuantity(), item.getUnitPrice()));
        }

        int orderId = orderService.placeOrder(1, orderItems); // Using userId 1 for now
        if (orderId > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order placed successfully! Order ID: " + orderId);
            handleClearCart();
            loadProducts(); // Refresh product list
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order.");
        }
    }

    private void updateTotals() {
        int itemCount = cartItems.size();
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        lblItemCount.setText(String.valueOf(itemCount));
        lblTotalAmount.setText(String.format("Rs. %.2f", total));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for cart items
    public static class CartItem {
        private int productId;
        private String productName;
        private double unitPrice;
        private int quantity;

        public CartItem(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getSubtotal() { return unitPrice * quantity; }
    }
}
