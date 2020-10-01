package com.kyle.helloworld.demo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;




public class Parser {

    public static LinkedHashMap<String, HashMap<String,String>> weatherForecast = new LinkedHashMap<>();
    public static LinkedHashMap<String, HashMap<String,String>> hourlyForecast = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> currentWeather = new LinkedHashMap<>();
    public static LinkedHashMap<String, HashMap<String,String>> dailyForecast = new LinkedHashMap<>();
    public static LinkedHashMap<String, HashMap<String,String>> newHourlyForecast = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> rainChances = new LinkedHashMap<>();
    public static LinkedHashMap<Integer, LinkedHashMap<String,String>> nwsForecast = new LinkedHashMap<>();

    /*public static void getHourlyForecast(String json, int timeOffset) throws JSONException, ParseException {
        String modifiedJson = "{\"hourly\":" + json + "}";
        JSONObject jo = new JSONObject(modifiedJson);
        JSONArray weather = jo.getJSONArray("hourly");
        for (int i = 0; i < weather.length(); i++) {
            LinkedHashMap<String, String> hour = new LinkedHashMap<>();
            JSONObject obj = weather.getJSONObject(i);
            hour.put("Temperature",obj.getJSONObject("temp").getString("value"));
            hour.put("Precipitation",obj.getJSONObject("precipitation").getString("value"));
            hour.put("Precipitation Type",obj.getJSONObject("precipitation_type").getString("value"));
            hour.put("Feels Like",obj.getJSONObject("feels_like").getString("value"));
            hour.put("Humidity",obj.getJSONObject("humidity").getString("value"));
            hour.put("Wind Speed",obj.getJSONObject("wind_gust").getString("value"));
            hour.put("Wind Direction",convertDegreesToDirection(obj.getJSONObject("wind_direction").getString("value")));
            hour.put("Visibility",obj.getJSONObject("visibility").getString("value"));
            hour.put("Cloud Cover",obj.getJSONObject("cloud_cover").getString("value"));
            //String ISO = obj.getJSONObject("observation_time").getString("value");
           // String time = convertIsoToStandard(ISO,true, timeOffset);
            String time = obj.getString("dt");
            newHourlyForecast.put(time, hour);
        }
    }*/


    public static void get7DayForecast(String json, boolean isUSA) throws JSONException {
        if(isUSA){
            //Object to hold entire aircraft JSON file
            boolean wasToday = false;
            System.out.println("Hi" + json);
            JSONObject jo = new JSONObject(json);
            JSONArray weather = jo.getJSONObject("properties").getJSONArray("periods");
            int dayCounter = 0;
            int nightCounter = 0;
            for (int i = 0; i < 14; i++) {
                JSONObject obj = weather.getJSONObject(i);
                String dayName = obj.getString("name");
                String detailedForecast = obj.getString("detailedForecast");
                if(!dayName.contains("night") && !dayName.contains("Night")  || (dayName.equals("Tonight") && wasToday==false)){
                    if(dayName.equals("Today") || dayName.contains("This")){
                        wasToday = true;
                    }
                    rainChances.put("rainChance"+dayCounter, getChanceOfRain(detailedForecast));
                    rainChances.put("detailedForecast"+dayCounter,detailedForecast);
                    //convertDescriptionToCelsius(detailedForecast);
                    dayCounter++;
                }
                else{
                    rainChances.put("nightDetailedForecast" +nightCounter,detailedForecast);
                    rainChances.put("nightDay" + nightCounter,dayName +  ":");
                    nightCounter++;
                }

            }
        }
        else{
            for (int i = 0; i<=7; i++) {
                rainChances.put("rainChance"+i, "");
                rainChances.put("detailedForecast"+i,"");
            }
        }

    }

    public static void getCurrentWeather(String json, int timeOffset) throws JSONException {
        String weatherDescriptionDetailed = "";
        String weatherDescriptionBrief = "";
        JSONObject jo = new JSONObject(json);
        JSONArray weather = jo.getJSONArray("weather");
        for (int i = 0; i < weather.length(); i++) {
            JSONObject obj = weather.getJSONObject(i);
            weatherDescriptionDetailed = obj.getString("description");
            weatherDescriptionBrief = obj.getString("main");
        }
        String temperature = String.valueOf(jo.getJSONObject("main").get("temp"));
        String feelsLike = String.valueOf(jo.getJSONObject("main").get("feels_like"));
        String pressure = String.valueOf(jo.getJSONObject("main").get("pressure"));
        String humidity = String.valueOf(jo.getJSONObject("main").get("humidity"));
        String visibility = "N/A";
        String Cvisibility = "N/A";
        if(jo.has("visibility")){
            visibility = convertMetersToMiles(String.valueOf(jo.get("visibility"))) + " mi";
            Cvisibility = convertMeterstoKM(String.valueOf(jo.get("visibility"))) + " km";

        }
        String windSpeed = String.valueOf(jo.getJSONObject("wind").get("speed"));

        String windDirection = "N/A";
        if(jo.getJSONObject("wind").has("deg")){
            windDirection = convertDegreesToDirection(String.valueOf(jo.getJSONObject("wind").get("deg")));
        }
        String cloudiness = String.valueOf(jo.getJSONObject("clouds").get("all"));
        String unixSunrise = String.valueOf(jo.getJSONObject("sys").get("sunrise"));
        String unixSunset = String.valueOf(jo.getJSONObject("sys").get("sunset"));
        String unixUpdateTime = String.valueOf(jo.get("dt"));
        String sunrise = convertToStandardTime(unixSunrise,false, true, false, false, timeOffset);
        String sunset = convertToStandardTime(unixSunset,false, true, false, false,timeOffset);
        String lastUpdateTime = convertToStandardTime(unixUpdateTime,false, false, true,false, timeOffset);
        currentWeather.put("Description", capitalizeLetters(weatherDescriptionDetailed));
        currentWeather.put("Brief Description", weatherDescriptionBrief);
        currentWeather.put("Temperature", (Math.round(Double.parseDouble(temperature))) + " \u00B0" + "F");
        currentWeather.put("CTemperature", convertFtoC(temperature,0) + " \u00B0" + "C");
        currentWeather.put("Feels Like", (Math.round(Double.parseDouble(feelsLike))) + " \u00B0" + "F");
        currentWeather.put("CFeels Like", convertFtoC(feelsLike,0) + " \u00B0" + "C");
        currentWeather.put("Pressure", pressure + " hPa");
        currentWeather.put("Humidity", humidity +"%");
        currentWeather.put("Visibility", visibility) ;
        currentWeather.put("Wind Speed", windSpeed + " mph");
        currentWeather.put("CWind Speed", convertMtoKM(windSpeed) + " km/h");
        currentWeather.put("Wind Direction", windDirection);
        currentWeather.put("Cloudiness", cloudiness +"%");
        currentWeather.put("Sunrise", sunrise);
        currentWeather.put("Sunset", sunset);
        currentWeather.put("day", lastUpdateTime);
        currentWeather.put("weatherIcon", getWeatherIcon(weatherDescriptionDetailed,unixUpdateTime,unixSunrise,unixSunset,rainChances,0));
    }
    public static void getHourlyWeather(String json, int timeOffset) throws JSONException {
        JSONObject jo = new JSONObject(json);
        JSONArray weather = jo.getJSONArray("hourly");
        for (int i = 0; i < weather.length(); i++) {
            LinkedHashMap<String, String> hour = new LinkedHashMap<>();
            JSONObject obj = weather.getJSONObject(i);
            String time = removeExtraZero(convertToStandardTime(String.valueOf(obj.get("dt")),false, false, false, true, timeOffset));
            String temp = String.valueOf(obj.get("temp"));
            String feelsLike = String.valueOf(obj.get("feels_like"));
            String humidity = String.valueOf(obj.get("humidity"));
            String dewPoint = String.valueOf(obj.get("dew_point"));
            String clouds = String.valueOf(obj.get("clouds"));
            String windSpeed = String.valueOf(obj.get("wind_speed"));
            String windDirection = "N/A";
            if(obj.has("wind_deg")){
                windDirection = convertDegreesToDirection(String.valueOf(obj.get("wind_deg")));
            }
            String rain = "0";
            String Crain = "0";
            if(obj.has("rain")){
                rain = convertMMtoIN(String.valueOf(obj.getJSONObject("rain").get("1h")));
                Crain = String.valueOf(obj.getJSONObject("rain").get("1h"));
            }
            String pressure = String.valueOf(obj.get("pressure"));
            String weatherDescriptionBrief = "";
            String weatherDescritpionDetailed = "";
            JSONArray weatherDesc = new JSONArray(String.valueOf(obj.get("weather")));
            for(int j = 0; j < 1; j++){
                JSONObject ob = weatherDesc.getJSONObject(j);
                weatherDescriptionBrief = ob.getString("main");
                weatherDescritpionDetailed = ob.getString("description");
            }
            hour.put("time", time);
            hour.put("Temperature", String.valueOf(Math.round(Double.parseDouble(temp))));
            hour.put("CTemperature", convertFtoC(temp,0));
            hour.put("rain",rain);
            hour.put("Crain",Crain);
            hour.put("clouds", clouds);

            /*hour.put("Feels Like", feelsLike +  " \u00B0" + "F");
            hour.put("Humidity,", humidity + "%");
            hour.put("Dew Point", dewPoint + " \u00B0" + "F");

            hour.put("Wind Speed", windSpeed + " mph");
            hour.put("Wind Direction", windDirection);
            hour.put("Rain", rain + " in");
            hour.put("Pressure", pressure + " hPa");
            hour.put("Detailed Description", weatherDescritpionDetailed);
            //hour.put("Brief Description", weatherDescriptionBrief);*/
            hourlyForecast.put(String.valueOf(i),hour);
        }
    }

    public static void getDailyWeather(String json, int timeOffset) throws JSONException {
        JSONObject jo = new JSONObject(json);
        JSONArray weather = jo.getJSONArray("daily");
        for (int i = 0; i < weather.length()-1; i++) {
            LinkedHashMap<String, String> day = new LinkedHashMap<>();
            JSONObject obj = weather.getJSONObject(i);
            String unixTime = String.valueOf(obj.get("dt"));
            String time = convertToStandardTime(unixTime,false, false, true, false, timeOffset);
            String unixSunrise = String.valueOf(obj.get("sunrise"));
            String unixSunset = String.valueOf(obj.get("sunset"));
            String sunrise = convertToStandardTime(unixSunrise,false, true, false, false, timeOffset);
            String sunset = convertToStandardTime(unixSunset,false, true, false, false, timeOffset);
            String maxTemp = String.valueOf(obj.getJSONObject("temp").get("max"));
            String minTemp = String.valueOf(obj.getJSONObject("temp").get("min"));
            String humidity = String.valueOf(obj.get("humidity"));
            String dewPoint = String.valueOf(obj.get("dew_point"));
            String clouds = String.valueOf(obj.get("clouds"));
            String windSpeed = String.valueOf(obj.get("wind_speed"));
            String windDirection = "N/A";
            if(obj.has("wind_deg")){
                windDirection = convertDegreesToDirection(String.valueOf(obj.get("wind_deg")));
            }
            String rain = "0";
            String Crain = "0";
            if(obj.has("rain")){
                rain = convertMMtoIN(String.valueOf(obj.get("rain")));
                Crain = String.valueOf(obj.get("rain"));
            }
            String pressure = String.valueOf(obj.get("pressure"));
            String weatherDescriptionBrief = "";
            String weatherDescritptionDetailed = "";
            JSONArray weatherDesc = new JSONArray(String.valueOf(obj.get("weather")));

            for(int j = 0; j < 1; j++){
                JSONObject ob = weatherDesc.getJSONObject(j);
                weatherDescriptionBrief = ob.getString("main");
                weatherDescritptionDetailed = ob.getString("description");
            }
            day.put("Day", time);
            day.put("Detailed Description", capitalizeLetters(weatherDescritptionDetailed));
            day.put("Max Temperature",(Math.round(Double.parseDouble(maxTemp))) +  " \u00B0" + "F");
            day.put("Min Temperature",(Math.round(Double.parseDouble(minTemp))) +  " \u00B0" + "F");
            day.put("CMax Temperature", convertFtoC(maxTemp,0) +  " \u00B0" + "C");
            day.put("CMin Temperature",convertFtoC(minTemp,0) +  " \u00B0" + "C");
            day.put("Humidity", humidity + "%");
            day.put("Dew Point", dewPoint + " \u00B0" + "F");
            day.put("CDew Point", convertFtoC(dewPoint,2) + " \u00B0" + "C");
            day.put("Clouds", clouds + "%");
            day.put("Wind Speed", windSpeed + " mph");
            day.put("CWind Speed", convertMtoKM(windSpeed) + " km/h");
            day.put("Wind Direction", windDirection);
            day.put("Rain", rain + " in");
            day.put("CRain", Crain + " mm");
            day.put("Pressure", pressure + " hPa");
            day.put("Sunrise", sunrise);
            day.put("Sunset", sunset);
            day.put("weatherIcon", getWeatherIcon(weatherDescritptionDetailed,unixTime,unixSunrise,unixSunset,rainChances,i));
            dailyForecast.put(String.valueOf(i),day);
        }
    }

    protected static String convertIsoToStandard(String ISO, boolean wantDate, int offset) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(ISO);
        return date.toString();
    }

    protected static String convertToStandardTime(String unixTime, boolean wantDate, boolean wantTime, boolean wantWeekDay, boolean wantHour, int offset){
        Long u = Long.parseLong(unixTime);
        // convert seconds to milliseconds
        Date date = new Date(u*1000L);
        // the format of your date
        SimpleDateFormat sdf;
        if(wantDate && wantTime){
            sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        }
        else if(wantDate){
            sdf = new SimpleDateFormat("MM/dd/yyyy");
        }
        else if(wantWeekDay){
            sdf = new SimpleDateFormat("EEEE");
        }
        else if(wantHour){
            sdf = new SimpleDateFormat("hh aa");
        }
        else{
           sdf = new SimpleDateFormat("hh:mm:ss aa");
        }

        // give a timezone reference for formatting (see comment at the bottom)
        if(offset >= 0){
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+" +offset));
        }
        else{
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" +offset));
        }
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
    private static String removeExtraZero(String hour){
        String returnTime = "";
        for(int i = 0; i < hour.length(); i++){
            if( i == 0 && hour.charAt(i)==('0')){

            }
            else{
                returnTime += hour.charAt(i);
            }
        }
        return returnTime;
    }
    private static String getChanceOfRain(String description){
        int index = description.indexOf("%");
        if(index != -1){
            return "Chance of Precip: "+description.substring(index-2,index+1);
        }
        else{
            return "Chance of Precip: 0 %";
        }
    }

    private static String convertDescriptionToCelsius(String description){
        boolean finished = false;
        String desc = description;
        String output = "";
        String firstTemp = "";
        int index  = 0;
        String celsiusTemp = "";
        while(!finished){
            index = desc.indexOf("near");
            if(index != -1){
                firstTemp = description.substring(index+5,index+7);
                celsiusTemp = convertFtoC(firstTemp,0);
                output = desc.replace(firstTemp, celsiusTemp);
                desc  = desc.substring(index+7);
            }
            index = desc.indexOf("around");
            if(index != -1){
                firstTemp = description.substring(index+7,index+9);
                celsiusTemp = convertFtoC(firstTemp,0);
                output = desc.replace(firstTemp, celsiusTemp);
                desc  = desc.substring(index+7);
            }
            else{
                finished = true;
            }

        }
        return output;
    }

    private static int parseRainChance(String chanceOfRain){
        String chance = "";
        int index = chanceOfRain.indexOf(": ");
        for(int i = index+2; i < chanceOfRain.length(); i++){
            if(chanceOfRain.charAt(i) != '%' && chanceOfRain.charAt(i) != ' '){
                chance += chanceOfRain.charAt(i);
            }
        }
        return Integer.parseInt(chance);
    }


    private static String convertMetersToMiles(String meters){
        return String.valueOf(Math.round(((Double.parseDouble(meters))/1609)));
    }
    private static String getWeatherIcon(String description, String currentUnixTime, String unixSunrise,String unixSunset,LinkedHashMap<String,String> nwsDescription,int day){
        String weatherIconName = "";
        long unixCurrentTime = Long.parseLong(currentUnixTime);
        long unixSunriseTime = Long.parseLong(unixSunrise);
        long unixSunsetTime = Long.parseLong(unixSunset);
        boolean isNight = unixCurrentTime > unixSunsetTime || unixCurrentTime < unixSunriseTime;
        if(description.contains("clear") && isNight) {
            weatherIconName = "clear-night.png";
        }
        else if(description.contains("clear")){
            weatherIconName = "clear.png";
        }
        else if(Dashboard.isUSA && parseRainChance(nwsDescription.get("rainChance"+day)) < 40){
            if(nwsDescription.get("detailedForecast"+day).contains("thunderstorm")){
                if(!isNight){
                    weatherIconName = "scattered-thunderstorms.png";
                }
                else{
                    weatherIconName = "night-scattered-thunderstorms.png";
                }
            }
            else if((description.contains("light rain") || description.contains("mist") || description.contains("moderate rain") || description.contains("rain"))){
                if(!isNight){
                    weatherIconName = "scattered-showers.png";
                }
                else{
                    weatherIconName = "night-scattered-showers.png";
                }
            }
            else if((description.contains("overcast"))){
                if(!isNight){
                    weatherIconName = "overcast.png";
                }
                else{
                    weatherIconName = "night-overcast.png";
                }
            }
            else if(description.contains("clouds") && isNight){
                weatherIconName = "night-clouds.png";
            }
            else if(description.contains("clouds")){
                weatherIconName = "scattered-clouds.png";
            }
            else{
                weatherIconName = "clouds.png";
            }
        }
        else if(description.contains("thunderstorm")){
            weatherIconName = "thunderstorm.png";
        }

        else if((description.contains("light rain") || description.contains("mist") || description.contains("drizzle"))){
            weatherIconName = "light-rain.png";
        }
        else if(description.contains("moderate rain")){
            weatherIconName = "moderate-rain.png";
        }
        else if(description.contains("rain")){
            weatherIconName = "strong-rain.png";
        }
        else if(description.contains("overcast") && isNight){
            weatherIconName = "night-overcast.png";
        }
        else if(description.contains("overcast")){
            weatherIconName = "overcast.png";
        }
        else if(description.contains("clouds") && isNight){
            weatherIconName = "night-clouds.png";
        }
        else if(description.contains("clouds")){
            weatherIconName = "scattered-clouds.png";
        }
        else{
            weatherIconName = "clear.png";
        }
        return weatherIconName;
    }

    private static String convertMMtoIN(String mm) {
        double in = Double.parseDouble(mm);
        double multiple = (in / 25.4) * 100;
        double rounded = Math.round(multiple);
        double finished = rounded / 100;
        return String.valueOf(finished);
    }

    private static String capitalizeLetters(String input){
        String output = "";
        for(int i = 0; i < input.length(); i++){
            if(i == 0 || input.charAt(i-1) == ' '){
                output += Character.toUpperCase(input.charAt(i));
            }
            else{
                output += input.charAt(i);
            }
        }
        return output;
    }

    private static String convertDegreesToDirection(String d){
        Double degrees = Double.parseDouble(d);
        if(degrees > 348.75 && degrees <= 11.25){ return "N"; }
        else if(degrees > 11.25 && degrees <= 33.75){ return "NNE"; }
        else if(degrees > 33.75 && degrees <= 56.25){ return "NE"; }
        else if(degrees > 56.25 && degrees <= 78.75){ return "ENE"; }
        else if(degrees  > 78.75 && degrees <= 101.25){ return "E"; }
        else if(degrees > 101.25 && degrees <= 123.75){ return "ESE"; }
        else if(degrees > 123.75 && degrees < 146.25){ return "SE"; }
        else if(degrees > 146.25 && degrees <= 168.75){ return "SSE"; }
        else if(degrees > 168.75 && degrees <= 191.25){ return "S"; }
        else if(degrees > 191.25 && degrees <= 213.75){ return "SSW"; }
        else if(degrees > 213.75 && degrees <= 236.25){ return "SW"; }
        else if(degrees > 236.25 && degrees <=258.75){ return "WSW"; }
        else if(degrees > 258.75 && degrees <= 281.25){ return "W"; }
        else if(degrees > 281.25 && degrees <= 303.75){ return "WNW"; }
        else if(degrees > 303.75 && degrees <= 326.25){ return "NW"; }
        else if(degrees > 326.25 && degrees <= 348.75){ return "NNW"; }
        return "N/A";
    }

    private static String convertFtoC(String degreesF, int numberOfPlaces){
        double temp = Double.parseDouble(degreesF);
        temp = (temp-32)*5/9;
        if(numberOfPlaces == 0){
            return String.valueOf(Math.round(temp));
        }
        else{
            return String.valueOf(roundToNDecimalPlaces(temp,numberOfPlaces));
        }

    }

    private static String convertMtoKM(String miles){
        double distance = Double.parseDouble(miles);
        distance = distance / 1.609;
        return String.valueOf(roundToNDecimalPlaces(distance,2));
    }

    private static String convertMeterstoKM(String m){
        double distance = Double.parseDouble(m);
        distance = distance/1000;
        return String.valueOf(distance);
    }

    private static double roundToNDecimalPlaces(double number, int places){
       double roundedNumber = Math.round(number*(Math.pow(10,places)));
       return roundedNumber/(Math.pow(10,places));
    }
}
