package com.arhip14.webnews.service;

import com.arhip14.webnews.dto.WeatherDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    public WeatherDTO getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Робимо запит до OpenWeather
            JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class, city, apiKey);

            if (response != null) {
                WeatherDTO weatherDTO = new WeatherDTO();
                weatherDTO.setCity(response.get("name").asText());

                // Температуру округлюємо до цілого числа
                double temp = response.get("main").get("temp").asDouble();
                weatherDTO.setTemperature((int) Math.round(temp));

                weatherDTO.setDescription(response.get("weather").get(0).get("description").asText());

                // Формуємо URL для іконки погоди
                String iconCode = response.get("weather").get(0).get("icon").asText();
                weatherDTO.setIconUrl("https://openweathermap.org/img/wn/" + iconCode + ".png");

                return weatherDTO;
            }
        } catch (Exception e) {
            System.err.println("Помилка отримання погоди: " + e.getMessage());
        }

        return null;
    }
}