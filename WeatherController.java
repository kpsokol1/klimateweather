package com.kyle.helloworld.demo;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.engine.CDATASectionStructureHandler;

import java.io.IOException;

@Controller
public class WeatherController {
    @RequestMapping(value = "/location/{city}", method = RequestMethod.GET)
    public String getWeatherData(@PathVariable("city") String city, Model model) throws Exception {
        model.addAttribute("cityName", city);
        Dashboard.preWeatherInit(city, true, true, true,false,0);
        String formattedCity = Dashboard.city;
        model.addAttribute("day0", Parser.currentWeather.get("day"));
        model.addAttribute("city", formattedCity);
        model.addAttribute("temp", Parser.currentWeather.get("Temperature"));
        model.addAttribute("Ctemp", Parser.currentWeather.get("CTemperature"));
        model.addAttribute("description",  Parser.currentWeather.get("Description"));
        model.addAttribute("feels_like","Feels Like: " + Parser.currentWeather.get("Feels Like"));
        model.addAttribute("Cfeels_like","Feels Like: " + Parser.currentWeather.get("CFeels Like"));
        model.addAttribute("pressure", "Pressure: " + Parser.currentWeather.get("Pressure"));
        model.addAttribute("humidity", "Humidity: " + Parser.currentWeather.get("Humidity"));
        model.addAttribute("visibility", "Visibility: " + Parser.currentWeather.get("Visibility"));
        model.addAttribute("Cvisibility", "Visibility: " + Parser.currentWeather.get("CVisibility"));
        model.addAttribute("wind", "Wind: " + Parser.currentWeather.get("Wind Speed") + " " + Parser.currentWeather.get("Wind Direction"));
        model.addAttribute("Cwind", "Wind: " + Parser.currentWeather.get("CWind Speed") + " " + Parser.currentWeather.get("Wind Direction"));
        model.addAttribute("clouds", "Clouds: " + Parser.currentWeather.get("Cloudiness"));
        model.addAttribute("sunrise", "Sunrise: " + Parser.currentWeather.get("Sunrise"));
        model.addAttribute("sunset", "Sunset: " + Parser.currentWeather.get("Sunset"));
        for(int i = 0; i < Parser.hourlyForecast.size(); i++){
            model.addAttribute("test" + i,Parser.hourlyForecast.get(String.valueOf(i)).get("Temperature"));
            model.addAttribute("Ctest" + i,Parser.hourlyForecast.get(String.valueOf(i)).get("CTemperature"));
            model.addAttribute("time" + i,Parser.hourlyForecast.get(String.valueOf(i)).get("time"));
            model.addAttribute("raino" + i,Parser.hourlyForecast.get(String.valueOf(i)).get("rain"));
            model.addAttribute("Craino" + i,Parser.hourlyForecast.get(String.valueOf(i)).get("Crain"));
            model.addAttribute("cloud" +i,Parser.hourlyForecast.get(String.valueOf(i)).get("clouds"));
        }



        for(int i = 0; i<=6; i++){
            if(i == 0){
                model.addAttribute("maxTemp", "High: " + Parser.dailyForecast.get(String.valueOf(i)).get("Max Temperature"));
                model.addAttribute("minTemp",  "Low: " + Parser.dailyForecast.get(String.valueOf(i)).get("Min Temperature"));
                model.addAttribute("CmaxTemp", "High: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMax Temperature"));
                model.addAttribute("CminTemp",  "Low: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMin Temperature"));
                model.addAttribute("foreTemp", Parser.dailyForecast.get(String.valueOf(i)).get("Max Temperature") + " / " + Parser.dailyForecast.get(String.valueOf(i)).get("Min Temperature"));
                model.addAttribute("rain", "Rain: " + Parser.dailyForecast.get(String.valueOf(i)).get("Rain"));
                model.addAttribute("Crain", "Rain: " + Parser.dailyForecast.get(String.valueOf(i)).get("CRain"));

                model.addAttribute("weatherIcon", Parser.currentWeather.get("weatherIcon"));
                model.addAttribute("day",  "Today");
                model.addAttribute("dewPoint", "Dew Point: " + Parser.dailyForecast.get(String.valueOf(i)).get("Dew Point"));
                if(Dashboard.isUSA){
                    model.addAttribute("Ndescription", Parser.rainChances.get("detailedForecast"+i));
                    model.addAttribute("NIdescription", Parser.rainChances.get("nightDetailedForecast"+i));
                    model.addAttribute("nightDay",  Parser.rainChances.get("nightDay"+i));
                    model.addAttribute("rainChance", Parser.rainChances.get("rainChance"+i));
                }
                else{
                    model.addAttribute("Ndescription", "");
                    model.addAttribute("NIdescription", "");
                    model.addAttribute("nightDay",   "");
                    model.addAttribute("rainChance", "");
                }

                String hi = "";
            }
            else{
                model.addAttribute("day" + i,  Parser.dailyForecast.get(String.valueOf(i)).get("Day"));
                model.addAttribute("description" + i,  Parser.dailyForecast.get(String.valueOf(i)).get("Detailed Description"));
                if(Dashboard.isUSA){
                    model.addAttribute("Ndescription" + i, Parser.rainChances.get("detailedForecast"+i));
                    model.addAttribute("NIdescription" + i, Parser.rainChances.get("nightDetailedForecast"+i));
                    model.addAttribute("nightDay" + i,  Parser.rainChances.get("nightDay"+i));
                    model.addAttribute("rainChance" + i, Parser.rainChances.get("rainChance"+i));
                }
                else{
                    model.addAttribute("Ndescription" + i, "");
                    model.addAttribute("NIdescription" + i, "");
                    model.addAttribute("nightDay" + i,  "");
                    model.addAttribute("rainChance" + i, "");
                }

                model.addAttribute("maxTemp" + i, "High: " + Parser.dailyForecast.get(String.valueOf(i)).get("Max Temperature"));
                model.addAttribute("minTemp" + i,  "Low: " + Parser.dailyForecast.get(String.valueOf(i)).get("Min Temperature"));
                model.addAttribute("CmaxTemp" +i, "High: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMax Temperature"));
                model.addAttribute("CminTemp" +i,  "Low: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMin Temperature"));
                model.addAttribute("foreTemp" + i, Parser.dailyForecast.get(String.valueOf(i)).get("Max Temperature") + " / " + Parser.dailyForecast.get(String.valueOf(i)).get("Min Temperature"));
                model.addAttribute("CforeTemp" + i, "High: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMax Temperature") + "\n" + "Low: " + Parser.dailyForecast.get(String.valueOf(i)).get("CMin Temperature"));
                model.addAttribute("humidity" + i,  "Humidity: " + Parser.dailyForecast.get(String.valueOf(i)).get("Humidity"));
                model.addAttribute("dewPoint" + i, "Dew Point: " + Parser.dailyForecast.get(String.valueOf(i)).get("Dew Point"));
                model.addAttribute("clouds" + i, "Clouds: " + Parser.dailyForecast.get(String.valueOf(i)).get("Clouds"));
                model.addAttribute("wind" + i, "Wind: " + Parser.dailyForecast.get(String.valueOf(i)).get("Wind Speed") + " " + Parser.dailyForecast.get(String.valueOf(i)).get("Wind Direction"));
                model.addAttribute("Cwind" + i, "Wind: " + Parser.dailyForecast.get(String.valueOf(i)).get("CWind Speed") + " " + Parser.dailyForecast.get(String.valueOf(i)).get("Wind Direction"));
                model.addAttribute("rain" + i, "Rain: " + Parser.dailyForecast.get(String.valueOf(i)).get("Rain"));
                model.addAttribute("Crain" + i, "Rain: " + Parser.dailyForecast.get(String.valueOf(i)).get("CRain"));
                model.addAttribute("sunrise" + i, "Sunrise: " + Parser.dailyForecast.get(String.valueOf(i)).get("Sunrise"));
                model.addAttribute("sunset" + i, "Sunset: " + Parser.dailyForecast.get(String.valueOf(i)).get("Sunset"));

                model.addAttribute("weatherIcon" + i, Parser.dailyForecast.get(String.valueOf(i)).get("weatherIcon"));
                String hi = "";
            }
        }
        return "weather";
    }

    @RequestMapping("/location")
    public String getWeatherData() throws Exception {
        return "weather";
    }
}
