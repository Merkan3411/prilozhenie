package com.cinema.ticket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://xvbjaflhuqchygvjwqoi.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh2YmphZmxodXFjaHlndmp3cW9pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzMjczODEsImV4cCI6MjA3OTkwMzM4MX0.JSuXgdBxsAPCeSQwJrZEe6vAwB7JdrmK0a8E-WDZ3n4";
    private static final String REST_URL = SUPABASE_URL + "/rest/v1";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Gson gson = new Gson();

    public static JsonArray select(String table, String query) throws Exception {
        String url = REST_URL + "/" + table + (query != null ? "?" + query : "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();


        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), JsonArray.class);
        } else {
            throw new Exception("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public static JsonElement selectRaw(String table, String query) throws Exception {
        String url = REST_URL + "/" + table + (query != null ? "?" + query : "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return gson.fromJson(response.body(), JsonElement.class);
        } else {
            throw new Exception("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public static JsonObject insert(String table, Map<String, Object> data) throws Exception {
        String url = REST_URL + "/" + table;
        String jsonBody = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonArray array = gson.fromJson(response.body(), JsonArray.class);
            return array.get(0).getAsJsonObject();
        } else {
            throw new Exception("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public static void update(String table, String filter, Map<String, Object> data) throws Exception {
        String url = REST_URL + "/" + table + "?" + filter;
        String jsonBody = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    public static void delete(String table, String filter) throws Exception {
        String url = REST_URL + "/" + table + "?" + filter;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Error: " + response.statusCode() + " - " + response.body());
        }
    }

    //загрузка файла в Supabase Storage
    public static String uploadFile(String bucketName, String fileName,
                                    byte[] fileBytes, String contentType) throws Exception {
        String url = SUPABASE_URL + "/storage/v1/object/" + bucketName + "/" + fileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", contentType)
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return SUPABASE_URL + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        } else {
            throw new Exception("Upload error: " + response.statusCode() + " - " + response.body());
        }
    }
}
