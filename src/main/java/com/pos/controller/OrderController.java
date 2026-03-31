package com.pos.controller;

import com.pos.model.OrderItem;
import com.pos.model.Product;
import com.pos.repository.impl.OrderRepositoryImpl;
import com.pos.repository.impl.ProductRepositoryImpl;
import com.pos.repository.impl.UserRepositoryImpl;
import com.pos.service.OrderService;
import com.pos.service.ProductService;
import com.pos.service.impl.OrderServiceImpl;
import com.pos.service.impl.ProductServiceImpl;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderController {

    @FXML
    private ComboBox<Product> cmbProduct;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Button btnAddToCart;

    @FXML
    private Button btnRemoveFromCart;

    @FXML
    private Button btnPlaceOrder;

    @FXML
    private Button btnClearCart;

    @FXML
    private TableView<CartItem> tblCart;

    @FXML
    private TableColumn<CartItem, String> colProductName;

    @FXML
    private TableColumn<CartItem, Integer> colQuantity;

    @FXML
    private TableColumn<CartItem, Double> colUnitPrice;

    @FXML
    private TableColumn<CartItem, Double> colSubtotal;

    @FXML
    private Label lblTotalAmount;

    @FXML
    private Label lblAvailableStock;

    private final OrderService orderService;
    private final ProductService productService;
    private ObservableList<CartItem> cartItems;
    private int currentUserId = 1; // Default user ID, should be set from login

    public OrderController() throws SQLException, ClassNotFoundException {
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        this.orderService = new OrderServiceImpl(new OrderRepositoryImpl(), productRepository);
        this.productService = new ProductServiceImpl(productRepository);
    }

    @FXML
    public void initialize() {
        cartItems = FXCollections.observableArrayList();
        setupProductComboBox();
        setupTableColumns();
        setupButtonActions();
        setupProductSelectionListener();
        tblCart.setItems(cartItems);
        updateTotalAmount();
    }

    private void setupProductComboBox() {
        List<Product> products = productService.getAllProducts();
        cmbProduct.setItems(FXCollections.observableArrayList(products));
        cmbProduct.setConverter(new StringConverter<Product>() {
            @Override
            public String toString(Product product) {
                if (product == null) return "";
                return product.getName() + " - Rs. " + product.getPrice();
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });
        cmbProduct.setPromptText("Select a product");
    }

    private void setupTableColumns() {
        colProductName.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProductName()));
        colQuantity.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colUnitPrice.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());
    }

    private void setupButtonActions() {
        btnAddToCart.setOnAction(event -> handleAddToCart());
        btnRemoveFromCart.setOnAction(event -> handleRemoveFromCart());
        btnPlaceOrder.setOnAction(event -> handlePlaceOrder());
        btnClearCart.setOnAction(event -> handleClearCart());
    }

    private void setupProductSelectionListener() {
        cmbProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblAvailableStock.setText("Available Stock: " + newVal.getQuantity());
            } else {
                lblAvailableStock.setText("Available Stock: -");
            }
        });
    }

    private void handleAddToCart() {
        Product selectedProduct = cmbProduct.getValue();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a product.");
            return;
        }

        String quantityText = txtQuantity.getText().trim();
        if (quantityText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter quantity.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Quantity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid quantity.");
            return;
        }

        // Check if quantity is available
        int alreadyInCart = getQuantityInCart(selectedProduct.getProductId());
        if (quantity + alreadyInCart > selectedProduct.getQuantity()) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Stock", 
                "Only " + (selectedProduct.getQuantity() - alreadyInCart) + " items available.");
            return;
        }

        // Check if product already in cart, update quantity
        for (CartItem item : cartItems) {
            if (item.getProductId() == selectedProduct.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                tblCart.refresh();
                updateTotalAmount();
                clearInputs();
                return;
            }
        }

        // Add new item to cart
        CartItem cartItem = new CartItem(
            selectedProduct.getProductId(),
            selectedProduct.getName(),
            quantity,
            selectedProduct.getPrice().doubleValue()
        );
        cartItems.add(cartItem);
        updateTotalAmount();
        clearInputs();
    }

    private int getQuantityInCart(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    private void handleRemoveFromCart() {
        CartItem selectedItem = tblCart.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to remove.");
            return;
        }
        cartItems.remove(selectedItem);
        updateTotalAmount();
    }

    private void handlePlaceOrder() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Cart is empty. Add items to place an order.");
            return;
        }

        // Convert cart items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                cartItem.getProductId(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice()
            );
            orderItems.add(orderItem);
        }

        int orderId = orderService.placeOrder(currentUserId, orderItems);
        if (orderId > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Order placed successfully!\nOrder ID: " + orderId);
            handleClearCart();
            refreshProducts();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order. Please try again.");
        }
    }

    private void handleClearCart() {
        cartItems.clear();
        updateTotalAmount();
        clearInputs();
    }

    private void clearInputs() {
        cmbProduct.setValue(null);
        txtQuantity.clear();
        lblAvailableStock.setText("Available Stock: -");
    }

    private void refreshProducts() {
        List<Product> products = productService.getAllProducts();
        cmbProduct.setItems(FXCollections.observableArrayList(products));
    }

    private void updateTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        lblTotalAmount.setText(String.format("Rs. %.2f", total));
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
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
        private int quantity;
        private double unitPrice;

        public CartItem(int productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getSubtotal() { return quantity * unitPrice; }
    }
}
