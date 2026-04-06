package com.example.pollutiontracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public abstract class BaseController {


    protected abstract Node getRootNode();


    protected void switchScene(String fxmlFile)  throws SceneSwitchExceptionController {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pollutiontracker/" + fxmlFile)
            );
            Parent root = loader.load();

            Stage stage = (Stage) getRootNode().getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

        } catch (Exception e) {
            throw new SceneSwitchExceptionController("Error loading FXML: ");
        }
    }


    protected void switchScene(ActionEvent event, String fxmlFile) throws SceneSwitchExceptionController {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pollutiontracker/" + fxmlFile)
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

        } catch (Exception e) {
            throw new SceneSwitchExceptionController("Error loading FXML: ");
        }
    }


    protected void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setStyle("-fx-text-fill: #ff4d6d; -fx-font-weight: bold;");
        }
    }


    protected void showSuccess(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setStyle("-fx-text-fill: #2de2a6; -fx-font-weight: bold;");
        }
    }


    protected boolean isEmpty(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}