package com.shemuel.timeline.tools.weather;

import com.shemuel.timeline.dto.WeatherResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherTool {

    @Resource
    private RestTemplate restTemplate;

    @Value("#{'${gaode.areas}'.split(',')}")
    private List<String> gaodeAreas;

    private static final String WEATHER_URL =
            "https://restapi.amap.com/v3/weather/weatherInfo?key=dc9a8412cdc58d95c2ab7e5aa87323b6&city=%s&extensions=all";

    @Tool(name = "weatherForecast",description = "调用本工具查询城市的天气预报")
    public List<WeatherResponse> checkWeather() {
        // 调用高德 API 或其他天气 API 查询 city 的天气
        // 返回一个自定义类型 WeatherForecast，包含是否下雨、降雨概率、建议等
        // 例如：
        log.info("checkWeather tool invoked,{}",  gaodeAreas);
        List<WeatherResponse> responses = gaodeAreas.stream().map(city -> {
            WeatherResponse weather = getWeather(city);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return weather;
        }).collect(Collectors.toList());

        return responses;
    }

    public WeatherResponse getWeather(String cityCode) {
        try {
            log.info("getWeather single invoked,{}",  cityCode);
            ResponseEntity<WeatherResponse> response =
                    restTemplate.getForEntity(String.format(WEATHER_URL, cityCode), WeatherResponse.class);
            WeatherResponse body = response.getBody();
            List<WeatherResponse.Forecast> forecasts = body.getForecasts();
            if (forecasts != null && !forecasts.isEmpty()){
                WeatherResponse.Forecast forecast = forecasts.get(0);
                List<WeatherResponse.Forecast> newForecasts = new ArrayList<>();
                newForecasts.add(forecast);
                body.setForecasts(newForecasts);
            }
            return body;
        } catch (Exception e) {
            log.info("getWeather single error,{}",  cityCode, e);
            WeatherResponse errorResponse = new WeatherResponse();
            errorResponse.setInfo("调用高德天气接口失败，请留意天气情况");
            return errorResponse;
        }
    }

}

