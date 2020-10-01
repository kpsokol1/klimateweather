package com.kyle.helloworld.demo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Dashboard {

    public static int count = 0;
    public static int timeOffset = 0; //Default timezone
    private static String radarTimesUrl = "https://api.rainviewer.com/public/maps.json";
    public static LinkedList<String> unixTimes = new LinkedList<>();
    private static String filePath = "C:/Users/kyle/Documents";
    public static ArrayList<String> times = new ArrayList<>();
    public static HashMap<String, Long> locations = new HashMap<>();
    public static boolean radarExists = false;
    public static String latitude = "";
    public static String longitude = "";
    public static String city = "ja";
    public static boolean isUSA = false;
    public static boolean folderCompleted = false;
    private static boolean initCompleted = false;
    public static String truelocation = "";
    private static boolean weatherCompleted = false;
    public static LinkedList<String> latitudes = new LinkedList<>();
    public static LinkedList<String> longitudes = new LinkedList<>();
    public static LinkedList<Integer> timeOffsets = new LinkedList<>();
    public static int coordCounter = 0;


    private static boolean locationNeedsUpdated(String location){
        if(location.equals(truelocation)){
            return false;
        }
        else{
            truelocation = location;
            return true;
        }
    }

    public static void preWeatherInit(String location, boolean wantForecast, boolean wantWeather, boolean wantFormattedCity, boolean haveCoordinates, int i) throws JSONException, InterruptedException, IOException, ParseException {
        if(haveCoordinates){
            latitude = Dashboard.latitudes.get(i);
            longitude = Dashboard.longitudes.get(i);
            timeOffset = Dashboard.timeOffsets.get(i);
            coordCounter++;
            truelocation = location;
        }
        else if (locationNeedsUpdated(location)) {
            try {
                int zip = Integer.parseInt(location);
                latitude = getLat(zip);

            } catch (Exception e) {
                latitude = getLat(location);
            }
            if (wantFormattedCity) {
                city = getFormattedLocation(latitude, longitude);
            }
            timeOffset = getTimezoneOffset(latitude, longitude);
        }
       if (wantWeather) {
            /*if (isUSA && !haveCoordinates) {
                showWeather("sevenDay", latitude, longitude);
            }*/
            showWeather("current", latitude, longitude);
            if (wantForecast) {
                showWeather("daily", latitude, longitude);
                showWeather("hourly", latitude, longitude);
            }
       }
    }

    public static String weather(String location) throws Exception {
        Dashboard.unixTimes.clear();
        GetData.getTimes(radarTimesUrl);
        String radarFileName = "radar";
        int zoomLevel = 6; //farthest-closest  //10 closest //6-8 pretty good
        String  radarZoom = String.valueOf(zoomLevel);
        String mapZoom = String.valueOf(Integer.parseInt(radarZoom) + 1);
        createRadar(filePath, location, radarFileName, zoomLevel, radarZoom, mapZoom, latitude, longitude);
        return GetData.generateDirectoryName(latitude,longitude);

    }

    private static void createRadar(String filePath, String location, String radarFileName, int zoomLevel, String radarZoom, String mapZoom, String latitude, String longitude) throws Exception {
        System.out.println("making radar...");
        Images.overlayImages(60, times,latitude,longitude,"4",timeOffset);
        System.out.println("finished");

    }

    private static String generateLocationCode(String lat, String lon){
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        return String.valueOf(latitude+longitude);
    }

    public static boolean checkRadarExists(String lat, String lon){
        String locationCode = generateLocationCode(lat,lon);
        if(locations.containsKey(locationCode) && System.currentTimeMillis() / 1000L - locations.get(locationCode) < 600000){
            return true;
        }
        return false;
    }

    private static void printHashMap(HashMap<String, String> h) {
        for (Map.Entry<String, String> entry : h.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + ": " + value);
        }
    }
    private static void printNestedHashMap(HashMap<String, HashMap<String,String>> h){
        for (Map.Entry<String, HashMap<String, String>> entry : h.entrySet()) {
            String key1 = entry.getKey();
            System.out.println(key1);
            for (Map.Entry<String, String> e : entry.getValue().entrySet()) {
                String key2 = e.getKey();
                String value = e.getValue();
                System.out.println("\t" +key2 + ": " + value);
            }
        }
    }

    private static void showWeather(String type, String lat, String lon) throws IOException, JSONException, ParseException {
        String coordinates = "";
        String jsonData = "";
        if(type.equals("current")){
            coordinates = generateAPICoordinates(false,lat,lon);
            jsonData = GetData.GetRequest("https://api.openweathermap.org/data/2.5/weather?"+coordinates+"&units=imperial&appid=7c9f9be6e106a8f0e32186b8abea5a04");
            Parser.getCurrentWeather(jsonData,timeOffset);
            //printHashMap(Parser.currentWeather);
        }
        else if(type.equals("hourly")){
            coordinates = generateAPICoordinates(false,lat,lon);
            jsonData = GetData.GetRequest("https://api.openweathermap.org/data/2.5/onecall?"+coordinates+"&exclude=current,minutely,daily&units=imperial&appid=7c9f9be6e106a8f0e32186b8abea5a04");
            Parser.getHourlyWeather(jsonData,timeOffset);
            printNestedHashMap(Parser.hourlyForecast);
        }
        /*else if(type.equals("hourly")){
            jsonData = GetData.GetRequest("https://api.openweathermap.org/data/2.5/onecall?"+coordinates+"&exclude=current,minutely,daily&units=imperial&appid=7c9f9be6e106a8f0e32186b8abea5a04");
            Parser.getHourlyWeather(jsonData,timeOffset);
            printNestedHashMap(Parser.newHourlyForecast);

        }*/
        else if(type.equals("daily")){
            coordinates = generateAPICoordinates(false,lat,lon);
            jsonData = GetData.GetRequest("https://api.openweathermap.org/data/2.5/onecall?"+coordinates+"&exclude=current,minutely,hourly&units=imperial&appid=7c9f9be6e106a8f0e32186b8abea5a04");
            Parser.getDailyWeather(jsonData,timeOffset);
            printNestedHashMap(Parser.dailyForecast);
        }
        else if(type.equals("sevenDay")){
                coordinates = generateAPICoordinates(true,lat,lon);
                if(!coordinates.equals("false")){
                    jsonData = GetData.GetRequest("https://api.weather.gov/gridpoints/"+coordinates+"/forecast");
                    System.out.println("hI" + jsonData);
                }
                /*while(jsonData.equals("failed")){
                    jsonData = GetData.GetRequest("https://api.weather.gov/gridpoints/"+coordinates+"/forecast");
                    System.out.println("failed");
                }*/

            Parser.get7DayForecast(jsonData, isUSA);
            printNestedHashMap(Parser.weatherForecast);

        }
    }
    private static String generateAPICoordinates(boolean isNWS, String latitude, String longitude) throws IOException, JSONException {
        if(isNWS){
            String firstUrl = "https://api.weather.gov/points/" +latitude+","+longitude;
            String data2 = GetData.GetRequest(firstUrl);
            JSONObject jo1;
            try{
                jo1 = new JSONObject(data2);
            }
            catch(JSONException e){
                data2 = GetData.GetRequest(firstUrl);
                jo1 = new JSONObject(data2);
            }

            String office = jo1.getJSONObject("properties").getString("cwa");
            String gridX = String.valueOf(jo1.getJSONObject("properties").get("gridX"));
            String gridY = String.valueOf(jo1.getJSONObject("properties").get("gridY"));
            if(gridX.equals("null") || gridY.equals("null")){
                Dashboard.isUSA = false;
                return "false";
            }
            return office +"/" + gridX + "," + gridY;
        }
        return "lat=" + latitude +"&lon=" + longitude;
    }
    private static String getLat(String location) throws IOException, JSONException {
        String data = "{\"coordinates\": " + GetData.GetRequest( "https://api.locationiq.com/v1/search.php?key=4c6be174d49686&q=" + location + "&format=json&limit=1") + "}";
        JSONObject jo = new JSONObject(data);
        JSONArray coordinates = jo.getJSONArray("coordinates");
        String latitude = "";
        for (int i = 0; i < 1; i++) {
            JSONObject obj = coordinates.getJSONObject(i);
            latitude = obj.getString("lat");
            longitude = obj.getString("lon");
        }
        return latitude;
    }
    private static String getLat(int zipcode) throws IOException, JSONException {
        String data = "{\"coordinates\": " + GetData.GetRequest( " https://us1.locationiq.com/v1/search.php?key=4c6be174d49686&postalcode=" + String.valueOf(zipcode) + "&countrycodes=us&format=json&limit=1") + "}";
        JSONObject jo = new JSONObject(data);
        JSONArray coordinates = jo.getJSONArray("coordinates");
        String latitude = "";
        for (int i = 0; i < 1; i++) {
            JSONObject obj = coordinates.getJSONObject(i);
            latitude = obj.getString("lat");
            longitude = obj.getString("lon");
        }
        return latitude;
    }
    private static String getLon(String location) throws IOException, JSONException, InterruptedException {
        Thread.sleep(2000);
        //String data = "{\"coordinates\": " + GetData.GetRequest( " https://us1.locationiq.com/v1/search.php?key=4c6be174d49686&q=" + location + "&format=json&limit=1") + "}";
        String data = "{\"coordinates\": " + GetData.GetRequest( "https://api.locationiq.com/v1/search.php?key=4c6be174d49686&q=" + location + "&format=json&normalizecity=1&limit=1") + "}";
        JSONObject jo = new JSONObject(data);
        JSONArray coordinates = jo.getJSONArray("coordinates");
        String longitude = "";
        for (int i = 0; i < 1; i++) {
            JSONObject obj = coordinates.getJSONObject(i);
            longitude = obj.getString("lon");
        }
        return longitude;
    }
    private static String getLon(int zipcode) throws JSONException, IOException, InterruptedException {
        Thread.sleep(2000);
        String data = "{\"coordinates\": " + GetData.GetRequest( " https://us1.locationiq.com/v1/search.php?key=4c6be174d49686&postalcode=" + String.valueOf(zipcode) + "&countrycodes=us&format=json&limit=1") + "}";
        JSONObject jo = new JSONObject(data);
        JSONArray coordinates = jo.getJSONArray("coordinates");
        String longitude = "";
        for (int i = 0; i < 1; i++) {
            JSONObject obj = coordinates.getJSONObject(i);
            longitude = obj.getString("lon");
        }
        return longitude;
    }

    protected static String getFormattedLocation(String lat, String lon) throws IOException, JSONException, InterruptedException {
        String url = "https://us1.locationiq.com/v1/reverse.php?key=4c6be174d49686&lat=" + lat+ "&lon=" + lon + "&format=json&normalizecity=1";
        Thread.sleep(2000);
        String data = GetData.GetRequest(url);
        System.out.println(data);
        JSONObject jo = new JSONObject(data);
        String output = "";
        JSONObject address = jo.getJSONObject("address");
        String city = "";
        if(address.has("city")){
            city = address.getString("city");
        }
        else if(address.has("locality"))
        {
            city = address.getString("locality");
        }

        String country = address.getString("country");
        if(country.equals("United States of America")){
            isUSA = false;
            return city + ", " + address.getString("state");
        }
        else if(country.equals("United Kingdom")){
            isUSA = false;
            return city + ", " + address.getString("state") + ", "  + country;
        }
        else{
            isUSA = false;
            return city + ", " + country;
        }
    }



    private static int secondsToHours(String seconds){
        return (Integer.valueOf(seconds))/3600;
    }

    private static int getTimezoneOffset(String lat, String lon) throws IOException, JSONException {
        System.out.println(lat);
        System.out.println(lon);
        String url = "http://api.timezonedb.com/v2.1/get-time-zone?key=1MDQ3UNNSJDG&format=json&by=position&lat=" +lat+ "&lng=" + lon;
        String data = GetData.GetRequest(url);
        String hi = "";
        JSONObject timeZoneOffset = new JSONObject(data);
        System.out.println(data);
        return secondsToHours(String.valueOf((timeZoneOffset.get("gmtOffset"))));
    }



}
