package com.arhip14.webnews.dto;

import lombok.Data;

@Data
public class WeatherDTO {
    private String city;
    private int temperature;
    private String description;
    private String iconUrl;
}