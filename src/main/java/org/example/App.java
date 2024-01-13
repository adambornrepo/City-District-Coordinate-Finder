package org.example;

import org.example.helper.DataExtractor;

public class App {
    public static void main(String[] args) {
        // Array of country ids to search
        // The IDs can be easily found from the resources/country.txt file
        int[] searchIds = {125, 145, 152};

        // In the responses of the OpenStreetMap API, country names can be written in a local format.
        // To better understand how they are written, refer to the 'Did you mean' logs.
        // If necessary, change the country name in the countries.txt file
        // I've tried to minimize the differences caused by characters not present in the English alphabet.
        // However, sometimes, due to the preference for local spelling of the city/district name and the usage of some symbols in the data,
        // the desired information may not be found even though it exists. So, you can check logs and statistics
        // and manually fill in the missing parts using the following URL:
        // "https://nominatim.openstreetmap.org/search?format=json&q=" + "searchParam"
        getCitiesDistrictsAndTheirCoordinatesByCountryIds(searchIds);
    }

    private static void getCitiesDistrictsAndTheirCoordinatesByCountryIds(int[] countryIds) {
        DataExtractor.extractDataByIds(countryIds);
    }
}
