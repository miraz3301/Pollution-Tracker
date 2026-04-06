package com.example.pollutiontracker;

// For API
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class HomePageController extends RootClass {

    public  String cCity;
    @FXML private BarChart<String, Number> weeklyChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Label dateLabel;
    @FXML private Label aqiValueLabel;
    @FXML private Label aqiStatusLabel;
    @FXML private Label co2Label;
    @FXML private Label no2Label;
    @FXML private Label nh3Label;
    @FXML private Label so2Label;
    @FXML
    private Label cityLabel;



    @Override
    protected Node getRootNode() {
        return aqiValueLabel;
    }
    String APICITY;


    @FXML
    public void initialize() {

        Integer userId = UserSession.getCurrentUserId();
        String city = null;

        if (userId != null) {
            UserDAO.UserProfile profile = UserDAO.getUserProfile(userId);

            if (profile != null) {
                city = profile.getDistrict();
            }
        }


        if (city == null || city.isBlank()) {
            city = " ";
            cityLabel.setText("Unknown City");
        } else {
            cityLabel.setText(city);
        }

        APICITY = city;

        // date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy");
        dateLabel.setText(today.format(formatter));

        // fetch API
        new Thread(() -> fetchAirQualityData()).start();
    }


    private void fetchAirQualityData() {

        String apiKey = "62221ceb05521152606ea8e22e5779d3639a73f2";
        //String city   = "dhaka";
        String city = APICITY;
        String url    = "https://api.waqi.info/feed/" + city + "/?token=" + apiKey;

        HttpClient  client  = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());

            if (json.getString("status").equals("ok")) {

                JSONObject data = json.getJSONObject("data");
                int        aqi  = data.getInt("aqi");
                JSONObject iaqi = data.getJSONObject("iaqi");


                Platform.runLater(() -> {

                    aqiValueLabel.setText(String.valueOf(aqi));
                    aqiStatusLabel.setText(getAqiStatus(aqi));
                    // Inside fetchAirQualityData -> Platform.runLater
                    String normalizedCity = APICITY.substring(0, 1).toUpperCase() + APICITY.substring(1).toLowerCase();
                    UserSession.setCity(normalizedCity);
                    UserSession.setAqi(aqi);
                    System.out.println(normalizedCity);
                    System.out.println(aqi);

                    if (iaqi.has("co"))
                        co2Label.setText(String.valueOf(
                                iaqi.getJSONObject("co").getDouble("v")));

                    if (iaqi.has("no2"))
                        no2Label.setText(String.valueOf(
                                iaqi.getJSONObject("no2").getDouble("v")));

                    if (iaqi.has("so2"))
                        so2Label.setText(String.valueOf(
                                iaqi.getJSONObject("so2").getDouble("v")));

                    if (iaqi.has("nh3"))
                        nh3Label.setText(String.valueOf(
                                iaqi.getJSONObject("nh3").getDouble("v")));

                    loadForecast(data);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();

            Platform.runLater(() -> {
                aqiValueLabel.setText("N/A");
                aqiStatusLabel.setText("Could not load data");
            });
        }
    }


    private void loadForecast(JSONObject data) {

        weeklyChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        JSONObject forecast = data.getJSONObject("forecast");
        JSONArray  daily    = forecast.getJSONObject("daily")
                .getJSONArray("pm25");

        for (int i = 0; i < 7 && i < daily.length(); i++) {

            JSONObject dayData   = daily.getJSONObject(i);
            String     fullDate  = dayData.getString("day");

            LocalDate date    = LocalDate.parse(fullDate);
            String    dayName = date.getDayOfWeek()
                    .getDisplayName(
                            java.time.format.TextStyle.SHORT,
                            java.util.Locale.ENGLISH
                    );

            String formattedDate = date.format(
                    DateTimeFormatter.ofPattern("dd MMM")
            );

            String day = dayName + "\n" + formattedDate;
            int    avg = dayData.getInt("avg");

            series.getData().add(new XYChart.Data<>(day, avg));
        }

        weeklyChart.getData().add(series);
        colorBars();
    }


    private void colorBars() {
        for (XYChart.Series<String, Number> s : weeklyChart.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                if (d.getNode() != null) {
                    applyBarColor(d.getNode(), d.getYValue().intValue());
                }
                d.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        applyBarColor(newNode, d.getYValue().intValue());
                    }
                });
            }
        }
    }


    private void applyBarColor(Node node, int value) {
        if (value >= 200) {
            node.setStyle("-fx-bar-fill: #ff6b6b;");   // red    — bad
        } else if (value >= 150) {
            node.setStyle("-fx-bar-fill: #fbbf24;");   // yellow — warning
        } else if (value >= 100) {
            node.setStyle("-fx-bar-fill: #6c63ff;");   // purple — moderate
        } else {
            node.setStyle("-fx-bar-fill: #4ade80;");   // green  — good
        }
    }


    private String getAqiStatus(int aqi) {
        if (aqi <= 50)       return "Air Quality is Good";
        else if (aqi <= 100) return "Air Quality is Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Air Quality is Poor";
        else if (aqi <= 300) return "Air Quality is Very Poor";
        else                 return "Air Quality is Hazardous";
    }




//    @FXML
//    public void handleHome() {
//        switchScene("homePage.fxml");
//    }

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

    @FXML
    public void handleProfile() {
        try {
            switchScene("Profile.fxml");
        }
        catch (SceneSwitchExceptionController e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }
}
