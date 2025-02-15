package com.rohit.flights.controller;

import com.rohit.flights.exception.BadRequestException;
import com.rohit.flights.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flights")
public class FlightsController {

    private final FlightService flightService;

    @Autowired
    public FlightsController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/fastest")
    public List<Map<String, Map<String, Integer>>> getTop5FastestFlights(@RequestParam String src, @RequestParam String dest) {
        if (src == null || dest == null || src.isEmpty() || dest.isEmpty()) {
            throw new BadRequestException("Source and destination airports must be provided");
        }

        src = src.toUpperCase();
        dest = dest.toUpperCase();
        return flightService.getTop5FastestFlights(src, dest);

    }
}