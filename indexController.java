package com.kyle.helloworld.demo;


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;

@Controller
public class indexController {
    @RequestMapping("/")
    public String getHomePage(Model model) throws Exception {
        Dashboard.coordCounter = 0;
        Dashboard.latitudes.clear();
        Dashboard.longitudes.clear();
        Dashboard.timeOffsets.clear();
        LinkedList<String> cities = new LinkedList<>();
        cities.add("New York City");
        Dashboard.latitudes.add("40.730610");
        Dashboard.longitudes.add("-73.935242");
        Dashboard.timeOffsets.add(-5);
        cities.add("Los Angeles");
        Dashboard.latitudes.add("34.052235");
        Dashboard.longitudes.add("-118.243683");
        Dashboard.timeOffsets.add(-7);
        cities.add("Sydney");
        Dashboard.latitudes.add("-33.865143");
        Dashboard.longitudes.add("151.209900");
        Dashboard.timeOffsets.add(10);
        cities.add("Tokyo");
        Dashboard.latitudes.add("35.652832");
        Dashboard.longitudes.add("139.839478");
        Dashboard.timeOffsets.add(9);
        cities.add("Hong Kong");
        Dashboard.latitudes.add("22.302711");
        Dashboard.longitudes.add("114.1772162");
        Dashboard.timeOffsets.add(8);
        cities.add("Paris");
        Dashboard.latitudes.add("48.864716");
        Dashboard.longitudes.add("2.349014");
        Dashboard.timeOffsets.add(1);
        cities.add("London, United Kingdom");
        Dashboard.latitudes.add("51.509865");
        Dashboard.longitudes.add("-0.118092");
        Dashboard.timeOffsets.add(1);



        for(int i = 0; i < cities.size()-1; i++){
            Dashboard.isUSA = false;
            Dashboard.preWeatherInit(cities.get(i),false,true, true,true, i);
            model.addAttribute("city" + i, cities.get(i));
            model.addAttribute("temp" + i, Parser.currentWeather.get("Temperature"));
            model.addAttribute("Ctemp" + i, Parser.currentWeather.get("CTemperature"));
            model.addAttribute("description" + i,  Parser.currentWeather.get("Description"));
            model.addAttribute("weatherIcon" + i, Parser.currentWeather.get("weatherIcon"));
        }
        Dashboard.preWeatherInit(cities.getLast(), false, true,true,true,6);
        model.addAttribute("temp", "Temperature: " + Parser.currentWeather.get("Temperature"));
        model.addAttribute("Ctemp", "Temperature: " + Parser.currentWeather.get("CTemperature"));
        model.addAttribute("description",  Parser.currentWeather.get("Description"));
        model.addAttribute("feels_like","Feels Like: " + Parser.currentWeather.get("Feels Like"));
        model.addAttribute("Cfeels_like","Feels Like: " + Parser.currentWeather.get("CFeels Like"));
        model.addAttribute("pressure", "Pressure: " + Parser.currentWeather.get("Pressure"));
        model.addAttribute("humidity", "Humidity: " + Parser.currentWeather.get("Humidity"));
        model.addAttribute("visibility", "Visibility: " + Parser.currentWeather.get("Visibility"));
        model.addAttribute("Cvisibility", "Visibility: " + Parser.currentWeather.get("CVisibility"));
        model.addAttribute("wind_speed", "Wind: " + Parser.currentWeather.get("Wind Speed") + " " + Parser.currentWeather.get("Wind Direction"));
        model.addAttribute("Cwind_speed", "Wind: " + Parser.currentWeather.get("CWind Speed") + " " + Parser.currentWeather.get("Wind Direction"));
        model.addAttribute("clouds", "Clouds: " + Parser.currentWeather.get("Cloudiness"));
        model.addAttribute("sunrise", "Sunrise: " + Parser.currentWeather.get("Sunrise"));
        model.addAttribute("sunset", "Sunset: " + Parser.currentWeather.get("Sunset"));

        /*String json = "{\"weatherIcon\"" +":" + "\"" + Parser.currentWeather.get("weatherIcon") + "\"" + "}";
        JSONObject jsonObject = new JSONObject("{\"weatherIcon\"" +":" + "\"" + Parser.currentWeather.get("weatherIcon") + "\"" + "}");
        String jsonStr = jsonObject.toString();*/
        model.addAttribute("weatherIcon", Parser.currentWeather.get("weatherIcon"));
        /*model.addAttribute("directoryName", Dashboard.weather("London, United Kingdom"));
        for(int i = 0; i < 13; i++){
            model.addAttribute("time" + i, Dashboard.unixTimes.get(i));
        }*/
        return "index";
    }

}
