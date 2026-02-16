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

    @FXML
    private ComboBox<String> cmbRole;

    private final UserLoginSer userLogin = new UserLoginImplSer();


    @FXML
    void ForgotPasswordOnAction(ActionEvent event) {


    }

    @FXML
    public void initialize() {
        cmbRole.getItems().addAll("Admin", "Staff");
    }

    @FXML
    void SignInOnAction(ActionEvent event) {


        String email = emailTxt.getText();
        String password = passwordTxt.getText();

        if (email.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter both email and password!").show();
            return;
        }

        UserDto userDto = new UserDto(email, password);
        try {
            boolean isVerified = userLogin.checkCredential(email, password);

            if (isVerified) {
                new Alert(Alert.AlertType.INFORMATION, "Login Successful!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Email or Password!").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
            e.printStackTrace();
        }


    }

    public void cmbRoleOnAction(ActionEvent actionEvent) {
            String selectedRole = cmbRole.getValue();
            if (selectedRole == null) {
                return;
            }

            try {
                Stage stage = (Stage) cmbRole.getScene().getWindow();

                if (selectedRole.equals("Admin")) {


                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Admin Verification");
                    dialog.setHeaderText("Protected Area");
                    dialog.setContentText("Enter Admin PIN:");


                    Optional<String> result = dialog.showAndWait();


                    if (result.isPresent() && result.get().equals("1234")) {

                        Parent root = FXMLLoader.load(getClass().getResource("/view/AdminLogin.fxml"));
                        stage.setScene(new Scene(root));
                        stage.centerOnScreen();
                        stage.show();

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Wrong PIN!").show();
                        cmbRole.setValue("Staff");
                    }

                } else if (selectedRole.equals("Staff")) {
                    Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
                    stage.setScene(new Scene(root));
                    stage.show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Page Load Error: " + e.getMessage()).show();
            }
        }
    }

