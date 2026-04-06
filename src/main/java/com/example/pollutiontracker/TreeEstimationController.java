package com.example.pollutiontracker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TreeEstimationController extends BaseController {

    @FXML private Label dateLabel;
    @FXML private Label treesPerPersonLabel;
    @FXML private Label treesPerAreaLabel;
    @FXML private Label totalTreesLabel;
    @FXML private Label cityTitleLabel;
    @FXML private Label aqiBadge;
    @FXML private ProgressBar personProgress;
    @FXML private ProgressBar areaProgress;

    private String APICITY;

    @Override
    protected Node getRootNode() {
        return dateLabel;
    }

    private static class CityData {
        double area;
        int population;
        CityData(double area, int population) {
            this.area = area;
            this.population = population;
        }
    }

    private static final Map<String, CityData> cityMap = new HashMap<>();

    static {
        cityMap.put("Dhaka", new CityData(306.4, 10000000));
        cityMap.put("Chittagong", new CityData(168.07, 5000000));
        cityMap.put("Khulna", new CityData(59.57, 1500000));
        cityMap.put("Rajshahi", new CityData(96.69, 800000));
        cityMap.put("Sylhet", new CityData(26.5, 700000));
        cityMap.put("Barisal", new CityData(58.0, 500000));
        cityMap.put("Rangpur", new CityData(205.0, 800000));
        cityMap.put("Mymensingh", new CityData(91.3, 600000));
    }

    @FXML
    public void initialize() {

        Integer userId = UserSession.getCurrentUserId();
        APICITY = "Dhaka";
        if (userId != null) {
            UserDAO.UserProfile profile = UserDAO.getUserProfile(userId);
            if (profile != null && profile.getDistrict() != null) {
                APICITY = profile.getDistrict();
            }
        }


        LocalDate today = LocalDate.now();
        dateLabel.setText(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));
        cityTitleLabel.setText("Tree Plantation Estimation — " + APICITY);
        new Thread(this::fetchAirQualityData).start();
    }

    private void fetchAirQualityData() {
        String apiKey = "62221ceb05521152606ea8e22e5779d3639a73f2";
        String url = "https://api.waqi.info/feed/" + APICITY + "/?token=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            if (json.getString("status").equals("ok")) {
                int aqi = json.getJSONObject("data").getInt("aqi");
                Platform.runLater(() -> performCalculations(aqi));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> aqiBadge.setText("AQI: N/A"));
        }
    }

    private void performCalculations(int aqi) {
        updateAqiBadge(aqi);
        CityData data = cityMap.getOrDefault(APICITY, cityMap.get("Dhaka"));
        int targetTreesPerPerson = Math.max(2, aqi / 25);
        double treesPerAreaTarget = (double) data.population / data.area;
        double totalTreesNeeded = (double) targetTreesPerPerson * data.population;
        treesPerPersonLabel.setText(String.valueOf(targetTreesPerPerson));
        treesPerAreaLabel.setText(String.format("%,.0f", treesPerAreaTarget));
        totalTreesLabel.setText(String.format("%.1fM", totalTreesNeeded / 1_000_000));
        double currentAvg = 2.4;
        personProgress.setProgress(Math.min(1.0, currentAvg / targetTreesPerPerson));
        areaProgress.setProgress(0.4);
    }

    private void updateAqiBadge(int aqi) {
        String status;
        String style;
        if (aqi <= 50) { status = "Good"; style = "health-badge-green"; }
        else if (aqi <= 100) { status = "Moderate"; style = "health-badge-yellow"; }
        else if (aqi <= 200) { status = "Unhealthy"; style = "health-badge-red"; }
        else { status = "Hazardous"; style = "health-badge-purple"; }
        aqiBadge.setText("AQI: " + aqi + " — " + status);
        aqiBadge.getStyleClass().removeAll("health-badge-red", "health-badge-green", "health-badge-yellow", "health-badge-purple");
        aqiBadge.getStyleClass().add(style);
    }

    @FXML public void handleHome() {
        try {
            switchScene("homePage.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }
    @FXML public void handleMessage() {
        try{switchScene("Message.fxml");}
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }}
    @FXML public void handleProfile() {
        try{switchScene("Profile.fxml"); }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }}
}