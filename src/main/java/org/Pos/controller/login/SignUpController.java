package org.Pos.controller.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML
    private ComboBox<?> selectRole;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtIdNumber;

    @FXML
    private TextField txtName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void createAccountOnAction(ActionEvent event) {

    }

}
