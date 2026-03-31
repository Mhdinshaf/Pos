package com.pos.controller;

import com.pos.model.Category;
import com.pos.repository.impl.CategoryRepositoryImpl;
import com.pos.service.CategoryService;
import com.pos.service.impl.CategoryServiceImpl;
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

public class CategoryController {

    @FXML
    private TableView<Category> tblCategories;

    @FXML
    private TableColumn<Category, Integer> colCategoryId;

    @FXML
    private TableColumn<Category, String> colCategoryName;

    @FXML
    private TextField txtCategoryName;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnBackToDashboard;

    private final CategoryService categoryService;
    private ObservableList<Category> categoryList;
    private FilteredList<Category> filteredList;
    private Category selectedCategory;

    public CategoryController() throws SQLException, ClassNotFoundException {
        this.categoryService = new CategoryServiceImpl(new CategoryRepositoryImpl());
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadCategories();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
    }

    private void setupTableColumns() {
        colCategoryId.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getCategoryId()).asObject());
        colCategoryName.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCategoryName()));
    }

    private void loadCategories() {
        categoryList = FXCollections.observableArrayList(categoryService.getAllCategories());
        filteredList = new FilteredList<>(categoryList, p -> true);
        tblCategories.setItems(filteredList);
    }

    private void setupTableSelection() {
        tblCategories.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCategory = newSelection;
                txtCategoryName.setText(newSelection.getCategoryName());
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(category -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return category.getCategoryName().toLowerCase().contains(lowerCaseFilter);
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
        String categoryName = txtCategoryName.getText().trim();

        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category name is required.");
            return;
        }

        boolean success = categoryService.addCategory(categoryName);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category added successfully.");
            loadCategories();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add category.");
        }
    }

    private void handleUpdate() {
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a category to update.");
            return;
        }

        String categoryName = txtCategoryName.getText().trim();

        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category name is required.");
            return;
        }

        boolean success = categoryService.updateCategory(selectedCategory.getCategoryId(), categoryName);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully.");
            loadCategories();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update category.");
        }
    }

    private void handleDelete() {
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a category to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete category: " + selectedCategory.getCategoryName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = categoryService.deleteCategory(selectedCategory.getCategoryId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully.");
                loadCategories();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete category. It may be in use by products.");
            }
        }
    }

    private void handleClear() {
        txtCategoryName.clear();
        txtSearch.clear();
        selectedCategory = null;
        tblCategories.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
