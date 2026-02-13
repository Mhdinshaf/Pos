package org.Pos.controller.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.Pos.Model.dto.UserDto;
import org.Pos.service.UserLoginSer;
import org.Pos.service.impl.UserLoginImplSer;

import java.io.IOException;
import java.sql.SQLException;

public class AdminLoginController {

    private Stage stage=new Stage();
    private final UserLoginSer userLoginSer=new UserLoginImplSer();

    @FXML
    private TextField emailTxt;

    @FXML
    private PasswordField passwordTxt;

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

        UserDto userDto = new UserDto(email, password);
        try {
            boolean isVerified = userLoginSer.checkCredential(email, password);

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


    @FXML
    void SignUoOnAction(ActionEvent event) throws SQLException{
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/SignUp.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            stage.show();
    }

}
