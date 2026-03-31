package com.pos.controller;

import com.pos.model.Supplier;
import com.pos.repository.impl.SupplierRepositoryImpl;
import com.pos.service.SupplierService;
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
import java.sql.SQLException;
import java.util.Optional;

public class SupplierController {

    @FXML private TableView<Supplier> tblSuppliers;
    @FXML private TableColumn<Supplier, Integer> colSupplierId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colEmail;
    @FXML private TableColumn<Supplier, String> colPhone;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnBackToDashboard;

    private final SupplierService supplierService;
    private ObservableList<Supplier> supplierList;
    private FilteredList<Supplier> filteredList;
    private Supplier selectedSupplier;

    public SupplierController() throws SQLException, ClassNotFoundException {
        this.supplierService = new SupplierServiceImpl(new SupplierRepositoryImpl());
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadSuppliers();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
    }

    private void setupTableColumns() {
        colSupplierId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSupplierId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
    }

    private void loadSuppliers() {
        supplierList = FXCollections.observableArrayList(supplierService.getAllSuppliers());
        filteredList = new FilteredList<>(supplierList, p -> true);
        tblSuppliers.setItems(filteredList);
    }

    private void setupTableSelection() {
        tblSuppliers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedSupplier = newSelection;
                txtName.setText(newSelection.getName());
                txtEmail.setText(newSelection.getEmail());
                txtPhone.setText(newSelection.getPhone());
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(supplier -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return supplier.getName().toLowerCase().contains(newValue.toLowerCase());
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
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, email, phone)) return;

        boolean success = supplierService.addSupplier(name, email, phone);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully.");
            loadSuppliers();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add supplier.");
        }
    }

    private void handleUpdate() {
        if (selectedSupplier == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a supplier to update.");
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, email, phone)) return;

        selectedSupplier.setName(name);
        selectedSupplier.setEmail(email);
        selectedSupplier.setPhone(phone);

        boolean success = supplierService.updateSupplier(selectedSupplier);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier updated successfully.");
            loadSuppliers();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update supplier.");
        }
    }

    private void handleDelete() {
        if (selectedSupplier == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a supplier to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete supplier: " + selectedSupplier.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = supplierService.deleteSupplier(selectedSupplier.getSupplierId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier deleted successfully.");
                loadSuppliers();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete supplier.");
            }
        }
    }

    private void handleClear() {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtSearch.clear();
        selectedSupplier = null;
        tblSuppliers.getSelectionModel().clearSelection();
    }

    private boolean validateInputs(String name, String email, String phone) {
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name is required.");
            return false;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Valid email is required.");
            return false;
        }
        if (phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Phone is required.");
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
