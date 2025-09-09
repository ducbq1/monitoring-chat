package com.example.demo;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AuthHelper {
    private static String token;
    private static long expiresAt;

    private static final String AUTH_URL = "http://localhost:9090/api/auth";

    public static synchronized String getToken(String username, String role, String clientName, String apiKey) throws Exception {
        // Return cached token if still valid
        if (token != null && !token.trim().isEmpty() && System.currentTimeMillis() < expiresAt) {
            return token;
        }

        int maxRetries = 3;
        int retryDelay = 2000; // milliseconds
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(AUTH_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                StringBuilder form = new StringBuilder();
                form.append("username=").append(URLEncoder.encode(username, "UTF-8"));
                form.append("&role=").append(URLEncoder.encode(role, "UTF-8"));
                form.append("&clientName=").append(URLEncoder.encode(clientName, "UTF-8"));
                form.append("&apiKey=").append(URLEncoder.encode(apiKey, "UTF-8"));

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(form.toString().getBytes("UTF-8"));
                    os.flush();
                }

                JSONObject data = getJsonObject(conn);
                token = data.optString("token", null);
                long expiresIn = data.optLong("expires_in", 0);

                if (token != null && !token.trim().isEmpty()) {
                    // Save expiry time slightly earlier to avoid edge cases
                    expiresAt = System.currentTimeMillis() + (expiresIn * 1000) - 5000;
                    return token;
                } else {
                    throw new Exception("Server returned empty token");
                }

            } catch (Exception ex) {
                lastException = ex;
                System.err.println("getToken attempt " + attempt + " failed: " + ex.getMessage());

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ignore) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                if (conn != null) conn.disconnect();
            }
        }

        // If still no token after all retries
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("JWT is null or empty after " + maxRetries + " attempts", lastException);
        }

        return token;
    }


    private static JSONObject getJsonObject(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder resp = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            resp.append(line);
        }
        reader.close();

        if (code != 200) {
            throw new RuntimeException("Auth failed, HTTP code: " + code + ", response: " + resp.toString());
        }

        // parse JSON
        JSONObject json = new JSONObject(resp.toString());
        JSONObject data = json.getJSONObject("data");
        return data;
    }

    public static synchronized void invalidateToken() {
        token = null;
        expiresAt = 0;
    }
}