package com.kyle.helloworld.demo;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class GetData
{
    //NOTE: this method was adapted from Shashank Bodkhe on dzone.com Link: https://dzone.com/articles/how-to-implement-get-and-post-request-through-simp
    public static String GetRequest(String url) throws IOException {
        URL urlForGetRequest = new URL(url);
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        int responseCode = conection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            return response.toString();
        }
        else {
            return "failed";
        }
    }

    public static BufferedImage saveImage(String imageUrl) throws IOException {
        BufferedImage image =null;
        try{
            URL url =new URL(imageUrl);
            // read the url
            image = ImageIO.read(url);
            return image;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static boolean saveImageToDrive(String imageUrl, String path) throws IOException {
    BufferedImage image =null;
    try{
        URL url =new URL(imageUrl);
        image = ImageIO.read(url);

        // for png
        ImageIO.write(image, "png",new File(path));
        return true;
    }
    catch(IOException e){
        e.printStackTrace();
        return false;
    }
}

    public static String saveMapsForLocation(String lat, String lon) throws IOException {
        String directoryName = generateDirectoryName(lat,lon);
        String url = "";
        if(!doesMapExist(directoryName)) {
            File file = new File("C:/Users/kyle/Documents/Hello_World/target/classes/static/maps/" + directoryName);
            file.mkdir();
            int zoom = 7;
            for (int j = 6; j <= 9; j++) {
                if (j == 7) {
                    zoom = 8;
                } else if (j == 8) {
                    zoom = 6;
                } else if (j == 9) {
                    zoom = 9;
                }
                url = "https://www.mapquestapi.com/staticmap/v5/map?key=0xT4ixRg9v8HBdsvkPdXMTHnoAXKPxzV&center=" + lat + "," + lon + "&size=512,512&zoom=" + zoom + "&scalebar=true";
                saveImageToDrive(url, "C:/Users/kyle/Documents/Hello_World/target/classes/static/maps/" + directoryName + "/" + zoom +".png");
            }
        }
        return directoryName;
    }

    public static HashMap<Integer, HashMap<String, String>> saveRadarImages(String lat, String lon, String radarType, int timeOffset) throws IOException{
        Dashboard.times.clear();
        HashMap<Integer, HashMap<String, String>> fullInfoNewRadarImages = new HashMap<>();

        String directoryName = generateDirectoryName(lat, lon);
        String url = "";
        String mainDirectory = "C:/Users/kyle/Documents/Hello_World/target/classes/static/images/" + directoryName;
        String radarZoom = "6";
        File file = new File(mainDirectory);
        if(!file.isDirectory()){
            file.mkdir();
        }
        for(int i = 0; i < 13; i++){
            HashMap<String, String> newRadarImages = new HashMap<>();
            radarZoom = "6";
            File tempFile = new File(mainDirectory + "/" + Dashboard.unixTimes.get(i));
            Dashboard.times.add(Parser.convertToStandardTime(Dashboard.unixTimes.get(i), false, true, false, false, timeOffset));
            if(!tempFile.isDirectory()) {
                tempFile.mkdir();
                for (int j = 5; j <= 8; j++) {
                    if (j == 6) {
                        radarZoom = "7";
                    } else if (j == 7) {
                        radarZoom = "5";
                    } else if (j == 8) {
                        radarZoom = "8";
                    }
                    String rZoom = radarZoom;

                    url = "https://tilecache.rainviewer.com/v2/radar/" + Dashboard.unixTimes.get(i) + "/512/" + rZoom + "/" + lat + "/" + lon + "/" + radarType + "/0_0.png";
                    saveImageToDrive(url, mainDirectory + "/" + Dashboard.unixTimes.get(i) + "/" + rZoom + "radar.png");
                    newRadarImages.put(rZoom, mainDirectory + "/" + Dashboard.unixTimes.get(i) + "/" + rZoom + "radar.png");
                    int index = i;
                    fullInfoNewRadarImages.put(index, newRadarImages);

                }
            }
        }
        return fullInfoNewRadarImages;
    }

    public static void getTimes(String timeUrl) throws IOException {
        Dashboard.unixTimes.clear();
        String currentTime = "";
        String json = GetRequest(timeUrl);
        json = json.substring(1,json.length()-1);
        Scanner sc = new Scanner(json);
        sc.useDelimiter(",");
        while(sc.hasNext()){
            currentTime = sc.next();
            Dashboard.unixTimes.add(currentTime);
        }
        sc.close();
    }

    private static boolean doesMapExist(String directoryName){
       File file = new File("C:/Users/kyle/Documents/Hello_World/target/classes/static/maps/"+directoryName);
       if(file.isDirectory()){
           return true;
       }
       else{
           return false;
       }
    }

    public static String generateDirectoryName(String lat, String lon){
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        double code = latitude + longitude;
        return String.valueOf(code);
    }

}
