package com.arhip14.webnews.controller;

import com.arhip14.webnews.dto.WeatherDTO;
import com.arhip14.webnews.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherDTO> getCurrentWeather(@RequestParam(defaultValue = "Kyiv") String city) {
        WeatherDTO weather = weatherService.getWeather(city);
        if (weather != null) {
            return ResponseEntity.ok(weather);
        }
        return ResponseEntity.status(503).build();
    }
}