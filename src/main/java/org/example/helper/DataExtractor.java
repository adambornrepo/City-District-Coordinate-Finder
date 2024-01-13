package org.example.helper;

import java.io.*;
import java.util.Arrays;

public class DataExtractor {

    static int TOTAL_CITY_FOUND = 0;
    static int TOTAL_CITY_MISSING_COORDINATE = 0;
    static int TOTAL_DISTRICT_FOUND = 0;
    static int TOTAL_DISTRICT_MISSING_COORDINATE = 0;

    public static void extractDataByIds(int[] ids) {
//        Arrays.sort(ids); (The data is alphabetically sorted. This part is optional)

        clearResultFiles();

        for (int countryId : ids) {
            String countryName = findCountryName(countryId);
            if (countryName == null) {
                System.err.println("COUNTRY NOT FOUND: " + countryId);
                continue;
            }
            System.out.println("Country found: " + countryName);
            findCity(countryId, countryName);


            System.err.printf("\n%28s : %s \n", "Statistics for country", countryName);
            System.err.printf("%28s : %d \n", "Total city found", TOTAL_CITY_FOUND);
            System.err.printf("%28s : %d \n", "City coordinate missing", TOTAL_CITY_MISSING_COORDINATE);
            System.err.printf("%28s : %d \n", "Total district found", TOTAL_DISTRICT_FOUND);
            System.err.printf("%28s : %d \n\n", "District coordinate missing", TOTAL_DISTRICT_MISSING_COORDINATE);

            TOTAL_CITY_FOUND = 0;
            TOTAL_CITY_MISSING_COORDINATE = 0;
            TOTAL_DISTRICT_FOUND = 0;
            TOTAL_DISTRICT_MISSING_COORDINATE = 0;
        }
        System.err.println("\n\n--- EXTRACTION COMPLETED ---");

    }

    private static int extractLastNumber(String line) {
        int lastIndex = line.lastIndexOf(' ');
        if (lastIndex != -1) {
            String lastNumberString = line.substring(lastIndex + 1, line.length() - 2);
            try {
                return Integer.parseInt(lastNumberString);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private static int extractFirstNumber(String line) {
        int start = line.indexOf('(') + 1;
        int end = line.indexOf(',');
        if (start != 0 && end != -1) {
            String id = line.substring(start, end);
            try {
                return Integer.parseInt(id);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private static String extractName(String line, boolean isCountry) {
        String namePart = isCountry ? line.split(",")[2] : line.split(",")[1];
        return namePart.substring(2, namePart.length() - 1).replace("/", "");
    }

    private static String findCountryName(int countryId) {
        try (BufferedReader countryReader = new BufferedReader(new FileReader("src/main/resources/countries.txt"))) {
            for (int i = 0; i < 9; i++) {
                countryReader.readLine();
            }
            String line;
            while ((line = countryReader.readLine()) != null) {
                int id = extractFirstNumber(line);
                if (id != -1 && id == countryId) {
                    return extractName(line, true);
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while reading/writing: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error occurred about number: " + e.getMessage());
        }
        return null;
    }

    private static void findCity(int countryId, String countryName) {
        try (BufferedReader cityReader = new BufferedReader(new FileReader("src/main/resources/cities.txt"));
             BufferedWriter cityWriter = new BufferedWriter(new FileWriter("src/main/java/org/example/result/city-result.txt", true));
        ) {
            for (int i = 0; i < 9; i++) {
                cityReader.readLine();
            }
            String line;
            while ((line = cityReader.readLine()) != null) {
                int num = extractLastNumber(line);

                if (num != -1 && num == countryId) {
                    String cityName = extractName(line, false);
                    int cityId = extractFirstNumber(line);
                    System.out.println("City found: " + cityName);
                    TOTAL_CITY_FOUND++;

                    String coordinate = CoordinateFinder.findCoordinate(cityName, cityName, countryName);
                    if (coordinate == null) {
                        TOTAL_CITY_MISSING_COORDINATE++;
                    }

                    String coordinated = line.substring(0, line.length() - 2) + (coordinate == null ? "" : coordinate) + line.substring(line.length() - 2);
                    cityWriter.write(coordinated);
                    cityWriter.newLine();

                    findDistrict(cityId, cityName, countryName);
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while reading/writing: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error occurred about number: " + e.getMessage());
        }
    }

    private static void findDistrict(int cityId, String cityName, String countryName) {

        try (BufferedReader districtReader = new BufferedReader(new FileReader("src/main/resources/districts.txt"));
             BufferedWriter districtWriter = new BufferedWriter(new FileWriter("src/main/java/org/example/result/district-result.txt", true));
        ) {
            for (int i = 0; i < 9; i++) {
                districtReader.readLine();
            }
            String line;
            while ((line = districtReader.readLine()) != null) {
                int num = extractLastNumber(line);

                if (num != -1 && num == cityId) {
                    String districtName = extractName(line, false);
                    System.out.println("District found: " + districtName);
                    TOTAL_DISTRICT_FOUND++;

                    String coordinate = CoordinateFinder.findCoordinate(districtName, cityName, countryName);
                    if (coordinate == null) {
                        TOTAL_DISTRICT_MISSING_COORDINATE++;
                    }
                    String coordinated = line.substring(0, line.length() - 2) + (coordinate == null ? "" : coordinate) + line.substring(line.length() - 2);
                    districtWriter.write(coordinated);
                    districtWriter.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while reading/writing: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error occurred about number: " + e.getMessage());
        }
    }

    private static void clearResultFiles() {
        try {
            FileWriter city = new FileWriter("src/main/java/org/example/result/city-result.txt");
            city.write("");
            city.close();
            FileWriter district = new FileWriter("src/main/java/org/example/result/district-result.txt");
            district.write("");
            district.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
