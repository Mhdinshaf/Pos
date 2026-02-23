package org.Pos.controller.login;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.Pos.Model.dto.UserDto;
import org.Pos.service.UserLoginSer;
import org.Pos.service.impl.UserLoginImplSer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


public class loginController {
    private Stage stage = new Stage();

    @FXML
    private TextField emailTxt;

    @FXML
    private PasswordField passwordTxt;


    private final UserLoginSer userLogin = new UserLoginImplSer();


    @FXML
    void ForgotPasswordOnAction(ActionEvent event) {
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
                boolean isVerified = userLogin.checkCredential(email, password);

                if (isVerified) {
                    String role = userLogin.getUserRole(email);


                    Stage stage = (Stage) emailTxt.getScene().getWindow();
                    Parent root = null;

                    if (role != null && role.equals("Admin")) {
                        root = FXMLLoader.load(getClass().getResource("/view/AdminLogin.fxml"));
                    } else {
                        root = FXMLLoader.load(getClass().getResource("/view/StaffDashboard.fxml"));
                    }


                    if (root != null) {
                        stage.setScene(new Scene(root));
                        stage.centerOnScreen();
                        stage.show();
                    }

                } else {
                    new Alert(Alert.AlertType.ERROR, "Invalid Email or Password!").show();
                }

            } catch (SQLException | IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }


    }






