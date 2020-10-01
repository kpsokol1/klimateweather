package com.kyle.helloworld.demo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Images
{
    public static void overlayImages(double transparency, ArrayList<String> times, String lat, String lon, String radarType, int timeOffset) throws IOException {
        String directoryName = GetData.saveMapsForLocation(lat,lon);
        HashMap<Integer, HashMap<String, String>> newRadarImages = GetData.saveRadarImages(lat,lon,radarType,timeOffset);
        String pathToRadarImage;
        int timeIndex;
        String rZoom;
        for (Map.Entry<Integer, HashMap<String, String>> entry : newRadarImages.entrySet()) {
                timeIndex = entry.getKey();
            for (Map.Entry<String, String> e : entry.getValue().entrySet()) {
                rZoom = e.getKey();
                pathToRadarImage = e.getValue();

                File mapImage = new File("C:/Users/kyle/Documents/Hello_World/target/classes/static/maps/" + directoryName + "/" + String.valueOf(Integer.parseInt(rZoom) + 1) +".png");
                File radarImage = new File(pathToRadarImage);

                BufferedImage background = ImageIO.read(mapImage);
                BufferedImage foreground = ImageIO.read(radarImage);

                //overlay and combine
                BufferedImage newImg = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = newImg.createGraphics();


// Draw the background image
                g.setComposite(AlphaComposite.SrcOver);
                g.drawImage(background, 0, 0, null);

// Draw the overlay image
                float alpha = (float) (transparency/100);
                g.setComposite(AlphaComposite.SrcOver.derive(alpha));
                g.drawImage(foreground, 0, 0, null);
                g.dispose();

                Graphics g1 = newImg.getGraphics();
                g1.setFont(g.getFont().deriveFont(20f));
                g1.setColor(Color.black);
                try{
                    g1.drawString(times.get(timeIndex),390,25);
                }
                catch(Exception er){
                    String hi = "";
                }

                g1.dispose();
                //write image
                try{
                    File f3 = new File(pathToRadarImage);
                    ImageIO.write(newImg,"png",f3);
                } catch (IOException er) {
                    er.printStackTrace();
                }
            }
        }

    }
}
