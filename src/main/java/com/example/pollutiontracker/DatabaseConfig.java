package com.example.pollutiontracker;

public final class DatabaseConfig {

    private static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:5432/pollution_tracker?sslmode=disable";
    private static final String DEFAULT_USERNAME = "postgres";
    private static final String DEFAULT_PASSWORD = "oyon4948";

    private DatabaseConfig() {
    }

    public static String getJdbcUrl() {
        return readSetting("PT_DB_URL", "pt.db.url", DEFAULT_JDBC_URL);
    }

    public static String getUsername() {
        return readSetting("PT_DB_USER", "pt.db.user", DEFAULT_USERNAME);
    }

    public static String getPassword() {
        return readSetting("PT_DB_PASSWORD", "pt.db.password", DEFAULT_PASSWORD);
    }

    public static String getAdminJdbcUrl() {
        String jdbcUrl = getJdbcUrl();
        int questionMarkIndex = jdbcUrl.indexOf('?');
        String suffix = questionMarkIndex >= 0 ? jdbcUrl.substring(questionMarkIndex) : "";
        String baseUrl = questionMarkIndex >= 0 ? jdbcUrl.substring(0, questionMarkIndex) : jdbcUrl;
        int lastSlashIndex = baseUrl.lastIndexOf('/');

        if (lastSlashIndex < 0) {
            throw new IllegalStateException("Invalid PostgreSQL JDBC URL: " + jdbcUrl);
        }

        return baseUrl.substring(0, lastSlashIndex + 1) + "postgres" + suffix;
    }

    public static String getDatabaseName() {
        String jdbcUrl = getJdbcUrl();
        int questionMarkIndex = jdbcUrl.indexOf('?');
        String baseUrl = questionMarkIndex >= 0 ? jdbcUrl.substring(0, questionMarkIndex) : jdbcUrl;
        int lastSlashIndex = baseUrl.lastIndexOf('/');

        if (lastSlashIndex < 0 || lastSlashIndex == baseUrl.length() - 1) {
            throw new IllegalStateException("Invalid PostgreSQL JDBC URL: " + jdbcUrl);
        }

        return baseUrl.substring(lastSlashIndex + 1);
    }

    public static boolean isLocalDatabase() {
        String jdbcUrl = getJdbcUrl().toLowerCase();
        return jdbcUrl.contains("://localhost:")
                || jdbcUrl.contains("://127.0.0.1:")
                || jdbcUrl.contains("://[::1]:");
    }

    private static String readSetting(String environmentVariable, String systemProperty, String defaultValue) {
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return defaultValue;
    }
}
