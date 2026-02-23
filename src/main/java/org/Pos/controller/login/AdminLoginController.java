package org.Pos.controller.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Pos.service.UserLoginSer;
import org.Pos.service.impl.UserLoginImplSer;

import java.io.IOException;
import java.sql.SQLException;

public class AdminLoginController {

    private final Stage stage = new Stage();
    private final UserLoginSer userLoginSer = new UserLoginImplSer();

    @FXML
    private TextField emailTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    @SuppressWarnings("unused")
    void ForgotPasswordOnAction(ActionEvent event) {
        // TODO: Implement forgot password functionality
    }

    @FXML
    void SignInOnAction(ActionEvent event) {

            String email = emailTxt.getText();
            String password = passwordTxt.getText();

            if (email.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please enter both email and password!").show();
                return;
            }

            try {
                boolean isVerified = userLoginSer.checkCredential(email, password);

                if (isVerified) {
                    new Alert(Alert.AlertType.INFORMATION, "Login Successful!").showAndWait();

                    Parent root = FXMLLoader.load(getClass().getResource("/view/AdminDashboard.fxml"));

                    Stage stage = (Stage) emailTxt.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.centerOnScreen();
                    stage.show();

                } else {
                    new Alert(Alert.AlertType.ERROR, "Invalid Email or Password!").show();
                }

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "System Error: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }

    public void SignUoOnAction(ActionEvent actionEvent) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/SignUp.fxml"));
            if (loader.getLocation() != null) {
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to load SignUp view!").show();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading SignUp: " + e.getMessage()).show();
            System.err.println("SignUp loading error: " + e.getMessage());
        }
    }
    }

