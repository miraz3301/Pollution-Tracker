package com.example.pollutiontracker;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileController extends RootClass {
    public static String currentCity;

    @FXML private Label dateLabel;
    @FXML private Label profileNameLabel;
    @FXML private Label profileLocationLabel;
    @FXML private Label profilePhoneLabel;
    @FXML private Label profileAgeBadge;
    @FXML private Label profileGenderBadge;

    @FXML private Label infoNameLabel;
    @FXML private Label infoAgeLabel;
    @FXML private Label infoGenderLabel;
    @FXML private Label infoPhoneLabel;
    @FXML private Label infoOutdoorLabel;

    @FXML private Label infoAsthmaLabel;
    @FXML private Label infoHeartLabel;
    @FXML private Label infoLungLabel;
    @FXML private Label infoAllergyLabel;
    @FXML private Label infoDiabetesLabel;

    @FXML private Label infoDivisionLabel;
    @FXML private Label infoDistrictLabel;
    @FXML private Label infoCityLabel;

    @Override
    protected Node getRootNode() {
        return dateLabel;
    }

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        dateLabel.setText(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));
        loadProfile();
    }

    private void loadProfile() {
        Integer userId = UserSession.getCurrentUserId();
        if (userId == null) {
            applyLoggedOutState();
            return;
        }

        UserDAO.UserProfile profile = UserDAO.getUserProfile(userId);
        if (profile == null) {
            applyMissingProfileState();
            return;
        }
        currentCity = profile.getDistrict();
        //System.out.println(currentCity);

        String displayName = firstNonBlank(profile.getFullName(), profile.getUsername(), "User");
        String location = joinNonBlank(", ", profile.getCity(), profile.getDistrict(), profile.getDivision());
        String phone = firstNonBlank(profile.getPhoneNumber(), profile.getEmail(), "Not provided");
        String ageText = profile.getAge() == null ? "Not set" : String.valueOf(profile.getAge());
        String genderText = firstNonBlank(profile.getGender(), "Not set");

        profileNameLabel.setText(displayName);
        profileLocationLabel.setText(location.isBlank() ? "Location not added yet" : location);
        profilePhoneLabel.setText(phone);
        profileAgeBadge.setText(profile.getAge() == null ? "Age: Not set" : "Age: " + profile.getAge());
        profileGenderBadge.setText(genderText);

        infoNameLabel.setText(displayName);
        infoAgeLabel.setText(ageText);
        infoGenderLabel.setText(genderText);
        infoPhoneLabel.setText(firstNonBlank(profile.getPhoneNumber(), "Not provided"));
        infoOutdoorLabel.setText(firstNonBlank(profile.getOutdoorActivity(), "Not set"));

        infoAsthmaLabel.setText(hasCondition(profile.getHealthInfo(), "asthma") ? "Yes" : "No");
        infoHeartLabel.setText(hasCondition(profile.getHealthInfo(), "heart disease") ? "Yes" : "No");
        infoLungLabel.setText(hasCondition(profile.getHealthInfo(), "lung disease", "copd") ? "Yes" : "No");
        infoAllergyLabel.setText(hasCondition(profile.getHealthInfo(), "allergies", "sinusitis") ? "Yes" : "No");
        infoDiabetesLabel.setText(hasCondition(profile.getHealthInfo(), "diabetes") ? "Yes" : "No");

        infoDivisionLabel.setText(firstNonBlank(profile.getDivision(), "Not set"));
        infoDistrictLabel.setText(firstNonBlank(profile.getDistrict(), "Not set"));
        infoCityLabel.setText(firstNonBlank(profile.getCity(), "Not set"));
    }

    private void applyLoggedOutState() {
        profileNameLabel.setText("No active session");
        profileLocationLabel.setText("Log in to view your profile.");
        profilePhoneLabel.setText("Not available");
        profileAgeBadge.setText("Age: -");
        profileGenderBadge.setText("Guest");
        applyEmptyDetails("Please log in");
    }

    private void applyMissingProfileState() {
        profileNameLabel.setText("Profile not found");
        profileLocationLabel.setText("We could not load your saved profile.");
        profilePhoneLabel.setText("Not available");
        profileAgeBadge.setText("Age: -");
        profileGenderBadge.setText("Unknown");
        applyEmptyDetails("Not available");
    }

    private void applyEmptyDetails(String value) {
        infoNameLabel.setText(value);
        infoAgeLabel.setText(value);
        infoGenderLabel.setText(value);
        infoPhoneLabel.setText(value);
        infoOutdoorLabel.setText(value);
        infoAsthmaLabel.setText(value);
        infoHeartLabel.setText(value);
        infoLungLabel.setText(value);
        infoAllergyLabel.setText(value);
        infoDiabetesLabel.setText(value);
        infoDivisionLabel.setText(value);
        infoDistrictLabel.setText(value);
        infoCityLabel.setText(value);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private String joinNonBlank(String separator, String... values) {
        List<String> parts = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                parts.add(value.trim());
            }
        }
        return String.join(separator, parts);
    }

    private boolean hasCondition(String healthInfo, String... keywords) {
        if (healthInfo == null || healthInfo.isBlank() || "none".equalsIgnoreCase(healthInfo.trim())) {
            return false;
        }

        String normalizedHealthInfo = healthInfo.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (normalizedHealthInfo.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }

        return false;
    }

    @FXML
    public void handleLogout() {
        UserSession.clear();
        try {
            switchScene("Welcome.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleHome() {
        try {
            switchScene("homePage.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleMessage() {
        try {
            switchScene("Message.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleTreeEstimation() {
        try {
            switchScene("TreeEstimation.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }
}
