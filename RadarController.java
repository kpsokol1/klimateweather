package com.kyle.helloworld.demo;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.CompletableFuture;

@Controller
public class RadarController
{

    @RequestMapping(value = "/location/{city}/radar", method = RequestMethod.GET)
    public String getWeatherData(@PathVariable("city") String city, Model model) throws Exception {
        if(!city.equals(Dashboard.truelocation)){
            Dashboard.preWeatherInit(city,false, false,true,false,0);
        }
        String cityName = Dashboard.city;
        model.addAttribute("cityName",city);
        model.addAttribute("city", cityName);
        model.addAttribute("enteredCity",city);
        /*model.addAttribute("directoryName", Dashboard.weather(city));
        for(int i = 0; i < 13; i++){
            model.addAttribute("time" + i, Dashboard.unixTimes.get(i));
        }*/
        model.addAttribute("latitude",Dashboard.latitude);
        model.addAttribute("longitude",Dashboard.longitude);
        return "newRadar";
    }
}
