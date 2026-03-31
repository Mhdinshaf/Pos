package com.pos.controller;

import com.pos.repository.impl.UserRepositoryImpl;
import com.pos.service.UserService;
import com.pos.service.impl.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private Button btnRegister;

    @FXML
    private Hyperlink linkLogin;

    @FXML
    private Button btnBack;

    private final UserService userService;

    public RegisterController() throws SQLException, ClassNotFoundException {
        this.userService = new UserServiceImpl(new UserRepositoryImpl());
    }

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "STAFF"));
        cmbRole.setValue("STAFF");
        
        btnRegister.setOnAction(this::handleRegister);
        linkLogin.setOnAction(this::handleBackToLogin);
    }

    private void handleRegister(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String role = cmbRole.getValue();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username cannot be empty.");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return;
        }

        if (role == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a role.");
            return;
        }

        boolean success = userService.register(username, password, role);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
            navigateToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists. Please choose a different username.");
        }
    }

    private void handleBackToLogin(ActionEvent event) {
        navigateToLogin();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Clothify Store - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
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
