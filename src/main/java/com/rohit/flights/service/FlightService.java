package com.rohit.flights.service;


import com.rohit.flights.model.Flight;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
    public class FlightService {

        private static final String CSV_FILE_PATH = "src/main/resources/ivtest-sched.csv";

        private List<Flight> flights;

        public FlightService() {
            try {
                this.flights = readFlightsFromCsv();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public List<Map<String, Map<String, Integer>>> getTop5FastestFlights(String src, String dest) {

            // Find all direct flights
        List<Flight> directFlights = flights.stream()
                .filter(flight -> flight.getFromAirportCode().equals(src) && flight.getToAirportCode().equals(dest))
                .sorted(Comparator.comparingInt(Flight::getDurationInMinutes))
                .collect(Collectors.toList());

        // Prepare the result list
        List<Map<String, Map<String, Integer>>> result = new ArrayList<>();

        // Add direct flights first
        for (Flight flight : directFlights) {
            Map<String, Map<String, Integer>> flightInfo = new HashMap<>();
            Map<String, Integer> flightDuration = new HashMap<>();
            flightDuration.put(flight.getFlightNo(), flight.getDurationInMinutes());
            flightInfo.put(src + "_" + dest, flightDuration);
            result.add(flightInfo);
        }



        List<Map<String, Map<String, Integer>>> connectingFlights = findConnectingFlights(src, dest, flights);

        result.addAll(connectingFlights);

        // Use a map to ensure unique routes and keep the fastest one
        Map<String, Map<String, Integer>> fastestRoutes = new HashMap<>();
        for (Map<String, Map<String, Integer>> route : result) {
            String key = route.keySet().iterator().next();
            int duration = route.values().iterator().next().values().iterator().next();
            if (!fastestRoutes.containsKey(key) || fastestRoutes.get(key).values().iterator().next() > duration) {
                fastestRoutes.put(key, route.get(key));
            }
        }

        List<Map<String, Map<String, Integer>>> sortedResults = fastestRoutes.entrySet().stream()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))

                .sorted((m1, m2) -> {
                    int duration1 = m1.values().iterator().next().values().iterator().next();
                    int duration2 = m2.values().iterator().next().values().iterator().next();
                    return Integer.compare(duration1, duration2);
                })
                .collect(Collectors.toList());

        // Return the top 5 fastest flights
        return sortedResults.stream().limit(5).collect(Collectors.toList());
    }

    private List<Map<String, Map<String, Integer>>> findConnectingFlights(String src, String dest, List<Flight> flights) {
        List<Map<String, Map<String, Integer>>> connectingFlights = new ArrayList<>();

        // Find all flights departing from the source
        List<Flight> departingFlights = flights.stream()
                .filter(flight -> flight.getFromAirportCode().equals(src))
                .collect(Collectors.toList());
        // For each departing flight, find the connecting flight
        for (Flight departFlight : departingFlights) {
            Map<String, Map<String, Integer>> connectionMap = new HashMap<>();
            // Filter flights that can connect to the departing flight's destination
            List<Flight> connectingFlightsFromDepart = flights.stream()
                    .filter(flight -> flight.getFromAirportCode().equals(departFlight.getToAirportCode()) && flight.getToAirportCode().equals(dest))
                    .collect(Collectors.toList());

            // Check if the layover time is enough (120 minutes)
            for (Flight connectFlight : connectingFlightsFromDepart) {
                int layoverTime = getLayoverTime(departFlight, connectFlight);

                if (layoverTime >= 120) { // Check for the 120 minutes minimum layover time

                    Map<String, Integer> connectionDurationMap = new HashMap<>();

                    int totalDuration = departFlight.getDurationInMinutes() + connectFlight.getDurationInMinutes() + layoverTime;
                    connectionDurationMap.put(departFlight.getFlightNo() + "_" + connectFlight.getFlightNo(), totalDuration);

                    String key = src + "_" + connectFlight.getFromAirportCode() + "_" + dest;
                    if (connectionMap.containsKey(key)) {
                        int existingDuration = connectionMap.get(key).values().iterator().next();
                        if (existingDuration > totalDuration) {
                            connectionMap.put(key, connectionDurationMap);
                        }
                    } else {

                        connectionMap.put(key, connectionDurationMap);
                        connectingFlights.add(connectionMap);
                    }
                }
            }

        }
        return connectingFlights;
    }

    // Calculate the layover time between two flights
    private int getLayoverTime(Flight departFlight, Flight connectFlight) {
        int arriveHour = departFlight.getEndTime() / 100;
        int arriveMinute = departFlight.getEndTime() % 100;
        int departHour = connectFlight.getStartTime() / 100;
        int departMinute = connectFlight.getStartTime() % 100;

        int arriveTotalMinutes = arriveHour * 60 + arriveMinute;
        int departTotalMinutes = departHour * 60 + departMinute;

        if (departTotalMinutes < arriveTotalMinutes) {
            departTotalMinutes += 24 * 60; // Adjust if departure time is the next day
        }

        return departTotalMinutes - arriveTotalMinutes;
    }

    private List<Flight> readFlightsFromCsv() throws IOException {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Flight flight = new Flight();
                flight.setFlightNo(data[0]);
                flight.setFromAirportCode(data[1]);
                flight.setToAirportCode(data[2]);
                flight.setStartTime(Integer.parseInt(data[3]));
                flight.setEndTime(Integer.parseInt(data[4]));
                flights.add(flight);
            }
        }
        return flights;
    }
}
