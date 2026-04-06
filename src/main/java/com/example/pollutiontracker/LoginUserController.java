package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class LoginUserController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @Override
    protected Node getRootNode() {
        return usernameField;
    }

    @FXML
    void loginAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (isEmpty(username, password)) {
            showError(messageLabel, "Username or email and password cannot be empty.");
            return;
        }

        UserDAO.AuthenticationResult result = UserDAO.login(username, password);

        if (result.isSuccess()) {
            UserSession.setCurrentUserId(result.getUserId());
            try {
                switchScene("homePage.fxml");
            }
            catch (SceneSwitchExceptionController e) {
                System.out.println("Navigation failed: " + e.getMessage());
            }
        } else {
            showError(messageLabel, "Invalid username/email or password.");
        }
    }

    @FXML
    void goToSignUp(ActionEvent event) {
        try {
            switchScene(event, "sign_up.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }
}
