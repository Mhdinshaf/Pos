package com.pos.controller;

import com.pos.model.Employee;
import com.pos.repository.impl.EmployeeRepositoryImpl;
import com.pos.service.EmployeeService;
import com.pos.service.impl.EmployeeServiceImpl;
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

public class EmployeeController {

    @FXML private TableView<Employee> tblEmployees;
    @FXML private TableColumn<Employee, Integer> colEmployeeId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colRole;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbRole;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnBackToDashboard;

    private final EmployeeService employeeService;
    private ObservableList<Employee> employeeList;
    private FilteredList<Employee> filteredList;
    private Employee selectedEmployee;

    public EmployeeController() throws SQLException, ClassNotFoundException {
        this.employeeService = new EmployeeServiceImpl(new EmployeeRepositoryImpl());
    }

    @FXML
    public void initialize() {
        setupRoleComboBox();
        setupTableColumns();
        loadEmployees();
        setupTableSelection();
        setupSearch();
        setupButtonActions();
    }

    private void setupRoleComboBox() {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "STAFF"));
    }

    private void setupTableColumns() {
        colEmployeeId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEmployeeId()).asObject());
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colPhone.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
    }

    private void loadEmployees() {
        employeeList = FXCollections.observableArrayList(employeeService.getAllEmployees());
        filteredList = new FilteredList<>(employeeList, p -> true);
        tblEmployees.setItems(filteredList);
        tblEmployees.refresh();
        // Reapply search filter if exists
        String searchText = txtSearch.getText();
        if (searchText != null && !searchText.isEmpty()) {
            filteredList.setPredicate(employee -> employee.getName().toLowerCase().contains(searchText.toLowerCase()));
        }
    }

    private void setupTableSelection() {
        tblEmployees.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEmployee = newSelection;
                txtName.setText(newSelection.getName());
                cmbRole.setValue(newSelection.getRole());
                txtEmail.setText(newSelection.getEmail());
                txtPhone.setText(newSelection.getPhone());
            }
        });
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(employee -> {
                if (newValue == null || newValue.isEmpty()) return true;
                return employee.getName().toLowerCase().contains(newValue.toLowerCase());
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
        String role = cmbRole.getValue();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, role, email, phone)) return;

        boolean success = employeeService.addEmployee(name, role, email, phone);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully.");
            loadEmployees();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add employee.");
        }
    }

    private void handleUpdate() {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an employee to update.");
            return;
        }

        String name = txtName.getText().trim();
        String role = cmbRole.getValue();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!validateInputs(name, role, email, phone)) return;

        selectedEmployee.setName(name);
        selectedEmployee.setRole(role);
        selectedEmployee.setEmail(email);
        selectedEmployee.setPhone(phone);

        boolean success = employeeService.updateEmployee(selectedEmployee);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully.");
            loadEmployees();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee.");
        }
    }

    private void handleDelete() {
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an employee to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete employee: " + selectedEmployee.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = employeeService.deleteEmployee(selectedEmployee.getEmployeeId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
                loadEmployees();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete employee.");
            }
        }
    }

    private void handleClear() {
        txtName.clear();
        cmbRole.setValue(null);
        txtEmail.clear();
        txtPhone.clear();
        selectedEmployee = null;
        tblEmployees.getSelectionModel().clearSelection();
        tblEmployees.refresh();
    }

    private boolean validateInputs(String name, String role, String email, String phone) {
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name is required.");
            return false;
        }
        if (role == null || role.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Role is required.");
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
