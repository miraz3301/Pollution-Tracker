package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.scene.Node;

public class wlcController extends RootClass {
    @Override
    protected Node getRootNode() {
        return null;
    }

    public void goToLogin(ActionEvent event) {
        try {
            switchScene(event, "login_user.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    public void goToSignUp(ActionEvent event) {
        try {
            switchScene(event, "sign_up.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }
}