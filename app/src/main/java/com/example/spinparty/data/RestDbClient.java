package com.example.spinparty.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;



public class RestDbClient {
    private static final int TIMEOUT_MS = 5000;

    private final String baseUrl;
    private final String apiKey;

    public RestDbClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public boolean isConfigured() {
        return baseUrl != null && !baseUrl.isEmpty() && apiKey != null && !apiKey.isEmpty();
    }

    public String get(String path) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("x-apikey", apiKey);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("Resposta HTTP " + code);
            }
            return readAll(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }
    public String post(String path, String jsonBody) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("x-apikey", apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            connection.getOutputStream().write(bytes);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_CREATED) {
                throw new IOException("Resposta HTTP " + code);
            }
            return readAll(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }
    public String put(String path, String jsonBody) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("PUT");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("x-apikey", apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            connection.getOutputStream().write(bytes);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_CREATED) {
                throw new IOException("Resposta HTTP " + code);
            }
            return readAll(connection.getInputStream());
        } finally {
            connection.disconnect();
        }
    }

    public void delete(String path) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("x-apikey", apiKey);

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new IOException("Resposta HTTP " + code);
            }
        } finally {
            connection.disconnect();
        }
    }
    private static String readAll(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
}
