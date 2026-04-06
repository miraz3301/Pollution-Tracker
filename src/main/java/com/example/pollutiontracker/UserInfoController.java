package com.example.pollutiontracker;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class UserInfoController extends RootClass {

    @FXML private TextField fullNameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField phoneField;

    @FXML private CheckBox asthmaCheck;
    @FXML private CheckBox heartCheck;
    @FXML private CheckBox lungCheck;
    @FXML private CheckBox allergyCheck;
    @FXML private CheckBox diabetesCheck;
    @FXML private CheckBox noneCheck;

    @FXML private ComboBox<String> outdoorCombo;
    @FXML private ComboBox<String> divisionCombo;
    @FXML private ComboBox<String> districtCombo;
    @FXML private TextField cityField;

    @FXML private Label messageLabel;

    private int userId;

    public void setUserId(int id) {
        this.userId = id;
    }


    @Override
    protected Node getRootNode() {
        return fullNameField;
    }


    @FXML
    public void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList(
                "Male", "Female", "Other"
        ));
        outdoorCombo.setItems(FXCollections.observableArrayList(
                "Less than 1 hour", "1-3 hours", "3-5 hours",
                "5-8 hours", "More than 8 hours"
        ));
        divisionCombo.setItems(FXCollections.observableArrayList(
                "Dhaka", "Chittagong", "Rajshahi", "Khulna",
                "Barisal", "Sylhet", "Rangpur", "Mymensingh"
        ));
        divisionCombo.setOnAction(e -> updateDistricts());
    }


    private void updateDistricts() {
        String division = divisionCombo.getValue();
        if (division == null) return;

        switch (division) {
            case "Dhaka":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Dhaka", "Gazipur", "Narayanganj", "Tangail", "Kishoreganj"));
                break;
            case "Chittagong":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Chittagong", "Cox's Bazar", "Comilla", "Feni", "Noakhali"));
                break;
            case "Rajshahi":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Rajshahi", "Bogra", "Pabna", "Sirajganj", "Natore"));
                break;
            case "Khulna":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Khulna", "Jessore", "Satkhira", "Kushtia", "Bagerhat"));
                break;
            case "Barisal":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Barisal", "Bhola", "Patuakhali", "Pirojpur"));
                break;
            case "Sylhet":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Sylhet", "Moulvibazar", "Habiganj", "Sunamganj"));
                break;
            case "Rangpur":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Rangpur", "Dinajpur", "Kurigram", "Gaibandha"));
                break;
            case "Mymensingh":
                districtCombo.setItems(FXCollections.observableArrayList(
                        "Mymensingh", "Jamalpur", "Sherpur", "Netrokona"));
                break;
            default:
                districtCombo.getItems().clear();
        }
    }


    @FXML
    public void handleSubmit() {
        if (userId <= 0) {
            showError(messageLabel, "Registration session not found. Please sign up again.");
            return;
        }

        if (isEmpty(
                fullNameField.getText(),
                ageField.getText(),
                cityField.getText()
        ) || genderCombo.getValue() == null
                || divisionCombo.getValue() == null
                || districtCombo.getValue() == null) {

            showError(messageLabel, "Please fill in all required fields!");
            return;
        }

        try {

            int age = Integer.parseInt(ageField.getText().trim());

            if (age <= 0 || age > 120) {
                showError(messageLabel, "Please enter a valid age!");
                return;
            }


            String fullName  = fullNameField.getText().trim();
            String gender    = genderCombo.getValue();
            String phone     = phoneField.getText().trim();
            String outdoor   = outdoorCombo.getValue();
            String division  = divisionCombo.getValue();
            String district  = districtCombo.getValue();
            String city      = cityField.getText().trim();
            String healthInfo = buildHealthInfo();


            System.out.println("=== User Info Submitted ===");
            System.out.println("Name     : " + fullName);
            System.out.println("Age      : " + age);
            System.out.println("Gender   : " + gender);
            System.out.println("Phone    : " + phone);
            System.out.println("Outdoor  : " + outdoor);
            System.out.println("Division : " + division);
            System.out.println("District : " + district);
            System.out.println("City     : " + city);
            System.out.println("Health   : " + healthInfo);
            System.out.println("===========================");

            boolean saved = UserDAO.saveUserInfo(
                    userId,
                    fullName,
                    age,
                    gender,
                    phone,
                    healthInfo,
                    outdoor,
                    division,
                    district,
                    city
            );

            if (!saved) {
                showError(messageLabel, "Could not save user information.");
                return;
            }

            showSuccess(messageLabel, "Information saved successfully!");
            switchScene("homePage.fxml");

        } catch (NumberFormatException e) {

            showError(messageLabel, "Age must be a valid number!");

        } catch (Exception e) {
            e.printStackTrace();
            showError(messageLabel, "Something went wrong!");
        }
    }

    private String buildHealthInfo() {
        if (noneCheck.isSelected()) {
            return "None";
        }

        List<String> conditions = new ArrayList<>();
        if (asthmaCheck.isSelected()) {
            conditions.add("Asthma");
        }
        if (heartCheck.isSelected()) {
            conditions.add("Heart Disease");
        }
        if (lungCheck.isSelected()) {
            conditions.add("Lung Disease (COPD)");
        }
        if (allergyCheck.isSelected()) {
            conditions.add("Allergies / Sinusitis");
        }
        if (diabetesCheck.isSelected()) {
            conditions.add("Diabetes");
        }

        return conditions.isEmpty() ? null : String.join(", ", conditions);
    }
}
