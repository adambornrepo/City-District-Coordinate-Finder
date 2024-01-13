package org.example.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.Scanner;

public class CoordinateFinder {
    public static String findCoordinate(String placeName, String cityName, String countryName) {
        try {
            String encodedPlaceName = URLEncoder.encode(placeName, "UTF-8");
            String endpoint = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedPlaceName;

            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");

            try (InputStream responseStream = connection.getInputStream();
                 Scanner scanner = new Scanner(responseStream, "UTF-8")) {

                String response = scanner.useDelimiter("\\A").next();
                return processResponse(response, cityName, countryName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String processResponse(String response, String cityName, String countryName) {
        try {
            String city = removeAccents(cityName);
            String country = removeAccents(countryName);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.isArray() && rootNode.size() > 0) {
                for (JsonNode result : rootNode) {
                    String displayName = removeAccents(result.get("display_name").asText());

                    if (displayName.contains(city) && displayName.contains(country)) {
                        double latitude = result.get("lat").asDouble();
                        double longitude = result.get("lon").asDouble();
                        return ", " + latitude + ", " + longitude;
                    } else {
                        System.out.println("\u001B[33mDid you mean: " + displayName + "\u001B[0m");
                    }
                }
            } else {
                System.out.println("No coordinate information found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String removeAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "");
    }
}
