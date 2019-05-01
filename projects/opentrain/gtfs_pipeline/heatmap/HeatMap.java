package projects.opentrain.gtfs_pipeline.heatmap;

import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

import com.google.startupos.common.flags.Flags;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeatMap {

  private List<StopsAround> stopsAroundList = new ArrayList<>();

  private double[][] heatMap;
  private List<Stop> stopsList = new ArrayList<>();
  private List<StopTime> stopTimeList = new ArrayList<>();
  private List<Trip> tripsList = new ArrayList<>();
  private static final String tripsPath = "projects/opentrain/gtfs_pipeline/trips.protobin";
  private static final String stopsPath = "projects/opentrain/gtfs_pipeline/stops.protobin";
  private static final String stopTimePath = "projects/opentrain/gtfs_pipeline/stopTime.protobin";

  @FlagDesc(
      name = "image_path",
      description = "Image path to map image.needs to contain .../<FileName>.png")
  public static Flag<String> imagePath = Flag.create("");
  @FlagDesc(
          name = "heat_map_path",
          description = "Image destination path to create heatmap image.needs to contain .../<FileName>.png")
  public static Flag<String> heatMapPath = Flag.create("");

  public HeatMap(int stopId, String time) throws IOException { // Time = HH:MM:ss

    dataToList();

    // From all the stops
    for (StopTime stopTime : stopTimeList) {
      // Pick the stop that i want, That start some trip, At the chosen time
      if (stopTime.getStopId() == stopId
          && Integer.parseInt(stopTime.getStopSequence()) == 1
          && stopTime.getDepartureTime().equals(time)) {

        // From all the trips
        for (Trip trip : tripsList) {

          // Pick the trip that starts on this stop
          if (stopTime.getTripId().equals(trip.getTripId())) {

            // From all the stops pick the stops on this trip
            for (StopTime tripStops : stopTimeList) {

              // Stops beside the chosen stop
              if (tripStops.getTripId().equals(trip.getTripId())
                  && tripStops.getStopId() != stopTime.getStopId()) {

                // ArrivalTime HH:mm:ss to double seconds
                String[] hhMMss = (tripStops.getArrivalTime().split(":"));
                Double arrivalTime =
                    Double.parseDouble(hhMMss[0]) * 3600
                        + // H
                        Double.parseDouble(hhMMss[1]) * 60 // m
                        + Double.parseDouble(hhMMss[2]); // s

                stopsAroundList.add(new StopsAround(tripStops, arrivalTime));
              }
            }
          }
        }
      }
    }
  }

  private void dataToList() throws IOException {
    try (FileInputStream tripsInputStream = new FileInputStream(new File(tripsPath));
        FileInputStream stopTimeInputStream = new FileInputStream(new File(stopTimePath));
        FileInputStream stopInputStream = new FileInputStream(new File(stopsPath))) {

      while (true) {
        Stop tmpStop = Stop.parseDelimitedFrom(stopInputStream);
        if (tmpStop == null) {
          break;
        }
        stopsList.add(tmpStop);
      }
      while (true) {
        StopTime tmpStopTime =
            StopTime.newBuilder().build().parseDelimitedFrom(stopTimeInputStream);
        if (tmpStopTime == null) {
          break;
        }
        stopTimeList.add(tmpStopTime);
      }
      while (true) {
        Trip tmpTrip =
            Trip.getDefaultInstance().newBuilder().build().parseDelimitedFrom(tripsInputStream);
        if (tmpTrip == null) {
          break;
        }
        tripsList.add(tmpTrip);
      }
    }
  }

  // The stop and his location on arrival_time matrix
  public class StopsAround {

    int i = 0; // Index on heatMap
    int j = 0;
    Stop stop;
    double arrivalTime;

    public StopsAround(StopTime stopTime, double arrivalTime) {

      this.arrivalTime = arrivalTime;
      setStop(stopTime);
    }

    private void setStop(StopTime stopTime) {

      // From all stops class pick the chosen stop from stopsTime
      // *to use longitude/latitude
      for (Stop stopAround : stopsList) {

        if (stopAround.getStopId() == stopTime.getStopId()) {
          stop = stopAround;
          break;
        }
      }
    }
  }

  // arrivalTime To Location by stops.latitude,stops.longitude make matrix of colors
  private void arrivalTimeToLocation() {

    double maxLongitude = 0;
    double maxLatitude = 0;

    for (StopsAround stp : stopsAroundList) {
      if (stp.stop.getStopLat() > maxLatitude) {
        maxLatitude = (stp.stop.getStopLat());
      }

      if (stp.stop.getStopLon() > maxLongitude) {
        maxLongitude = stp.stop.getStopLon();
      }
    }
    // maxLongitude = 35.23; //Max Longitude point on map(in the borders)
    // maxLatitude = 32.75; //Max Latitude point on map(in the borders)

    // Location matrix *100 to make difference between points
    heatMap =
        new double[(int) (maxLatitude * 100) * 2]
            [(int) (maxLongitude * 100) * 2]; // *2 to up and down, *2 right and left

    for (StopsAround stp : stopsAroundList) {

      // Arrival time of each stop from the chosen stop by location
      // *100 to make difference between location
      stp.i = (int) (stp.stop.getStopLat() * 100);
      stp.j = (int) (stp.stop.getStopLon() * 100);
      heatMap[stp.i][stp.j] = (int) (stp.arrivalTime);
    }

    // Distance of other points to the nearest stop

    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {

        if (heatMap[i][j] == 0) // not a stop
        {
          StopsAround firstStop = stopsAroundList.get(0);
          double ac = Math.abs(0 - firstStop.i); // y distance
          double cb = Math.abs(0 - firstStop.j); // x distance
          double distance = Math.hypot(ac, cb);
          double stopTimeArrival = firstStop.arrivalTime;

          // Find nearest stop
          for (StopsAround stp : stopsAroundList) {

            ac = Math.abs(i - stp.i); // y distance
            cb = Math.abs(j - stp.j); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb);
              stopTimeArrival = stp.arrivalTime;
            }
          }
          // *60 minutes to seconds
          heatMap[i][j] = (int) ((distance * 60) + stopTimeArrival);
        }
        if (heatMap[i][j] == 0) {
          System.out.println("Check heatMap assignment");
        }
      }
    }
  }

  // Draw only the heat map colors
  public void drawMap(String imagePath) {

    // color1 to color2
    Color color1 = new Color(255, 208, 235);
    Color color2 = new Color(126, 0, 67);

    // Normalize data
    double maxData = 0;
    double minData = heatMap[10][10];
    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (maxData < heatMap[i][j]) {
          maxData = heatMap[i][j];
        }
        if (heatMap[i][j] != 0 && minData > heatMap[i][j]) {
          minData = heatMap[i][j];
        }
      }
    }

    // Create image /** TODO: Try different size of image
    final BufferedImage image =
        new BufferedImage(heatMap.length, heatMap[0].length, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();

    // Draw the map
    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (heatMap[i][j] == 0) {
          System.out.println("ERROR:" + "\n" + "heatmap matrix value should not be 0");
        }

        float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

        int r =
            (int)
                (((float) color2.getRed() - (float) color1.getRed()) * value
                    + (float) color1.getRed());
        int g =
            (int)
                (((float) color2.getGreen() - (float) color1.getGreen()) * value
                    + (float) color1.getGreen());
        int b =
            (int)
                (((float) color2.getBlue() - (float) color1.getBlue()) * value
                    + (float) color1.getBlue());

        Color color = new Color(r, g, b);
        graphics2D.setPaint(color);
        graphics2D.fillRect(i, j, 2, 2);
      }
    }
    // Draw "X" on stops location
    for (StopsAround stp : stopsAroundList) {
      graphics2D.setPaint(Color.black);
      graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 20));
      graphics2D.drawString("X", stp.i - 1, stp.j - 1);
    }

    graphics2D.dispose();
    // Save file
    boolean createImage = false;
    try {
      createImage =
          ImageIO.write(
              image,
              "png",
              new File(
                      heatMapPath.get()));

    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!createImage) {
      System.out.println("can't create image");
    }
  }

  /* --------Old method to draw heatmap on map------

  private void arrivalTimeToLocationForMap () {

    //Right high 32.053978 34.784839
    //Left high 32.756847, 33.650970
    //Right low 31.155445, 35.212540
    //Left low 31.224366, 33.928004
    double maxLongitude = 35.23; //Max Longitude point on map(in the borders)
    double maxLatitude = 32.75; //Max Latitude point on map(in the borders)

    // Location matrix *100 to make difference between points,*2 for two sides of the map
    heatMap = new double[(int) (maxLatitude * 100)][(int) (maxLongitude * 100)];

    for (StopsAround stp : stopsAroundList) {

      // Arrival time of each stop from the chosen stop by location
      // *100 to make difference between location
      stp.i = (int) (stp.stop.getStopLat() * 100);
      stp.j = (int) (stp.stop.getStopLon() * 100);
      heatMap[stp.i][stp.j] = (int) (stp.arrivalTime);
    }

    // Distance of other points to the nearest stop

    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {

        if (heatMap[i][j] == 0) // not a stop
        {
          StopsAround firstStop = stopsAroundList.get(0);
          double ac = Math.abs(0 - firstStop.i); // y distance
          double cb = Math.abs(0 - firstStop.j); // x distance
          double distance = Math.hypot(ac, cb);
          double stopTimeArrival = firstStop.arrivalTime;

          // Find nearest stop
          for (StopsAround stp : stopsAroundList) {

            ac = Math.abs(i - stp.i); // y distance
            cb = Math.abs(j - stp.j); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb);
              stopTimeArrival = stp.arrivalTime;
            }
          }
          // *60 minutes to seconds
          heatMap[i][j] = (int) ((distance * 60) + stopTimeArrival);
        }
        if (heatMap[i][j] == 0) {
          System.out.println("Check heatMap assignment");
        }
      }
    }
  }*/

  // Functions by different data and sites to get x pixel on the map:
  // X = 31.84931551 x1^2 - 155.5785894 x1 x2 + 134.2697945 x2^2 + 3427.951862 x1 - 3846.07897 x2 +
  // 2512.552383
  public int getXpixel(double longitude, double latitude) {
    int xPixel =
        (int)
            ((31.84931551 * (Math.pow(longitude, 2)))
                + (-155.5785894 * longitude * latitude)
                + (134.2697945 * (Math.pow(latitude, 2)))
                + (3427.951862 * longitude)
                - (3846.07897 * latitude)
                + 2512.552383);
    /*#2(int)( -15980 + (69.73)*longitude + (405.4)*latitude);*/
    /*#1
    (int)
            (31.84931551 * (Math.pow(longitude, 2))
                    + -155.5785894 * longitude * latitude
                    + 134.2697945 * (Math.pow(latitude, 2))
                    + 3427.951862 * longitude
                    - 3846.07897 * latitude
                    + 2512.552383);*/
    return // xPixel;
    xPixel + 58;
  }

  // Functions by different data and sites to get y pixel on the map:
  // Y = 67.87903886 x1^2 - 59.89376472 x1 x2 + 11.68179254 x2^2 - 2895.823625 x1 + 1070.268543 x2 +
  // 38611.28426
  public int getYpixel(double longitude, double latitude) {
    int yPixel =
        (int)
            ((67.87903886 * (Math.pow(longitude, 2)))
                + (-59.89376472 * longitude * latitude)
                + (11.68179254 * (Math.pow(latitude, 2)))
                + (-2895.823625 * longitude)
                + (1070.268543 * latitude)
                + 38611.28426);
    /*#2(int)(23100 + (-653.6)*longitude + (-58.28)*latitude);*/
    /*#1
    (int)
            ((67.87903886 * (Math.pow(longitude, 2)))
                    + (-59.89376472 * longitude * latitude)
                    + (11.68179254 * (Math.pow(latitude, 2)))
                    + (-2895.823625 * longitude)
                    + (1070.268543 * latitude)
                    + 38611.28426);*/

    return // yPixel;
    yPixel + 53;
  }

  /*-----Old method to draw heatmap on map-----

   public void drawOnRealMap (String imagePath){

     //TODO: get real map size ---------------Need more data to not get out of the map
     System.out.println("l=" + heatMap.length + "j=" + heatMap[0].length);
     int maxX = 0;
     int maxY = 0;
     int iX = 0, jX = 0, iY = 0, jY = 0;
     for (int i = 0; i < heatMap.length - 1; i++) {    //TODO: Try function heatmap value to x,y---------
       for (int j = 0; j < heatMap[0].length - 1; j++) {
         if (maxX <
         getXpixel(j / 100, i / 100)) {
           maxX =
           getXpixel(j / 100, i / 100);
           iX = i;
           jX = j;
         }
         if (maxY <
         getXpixel(j / 100, i / 100)) {
           maxY = getYpixel(j / 100, i / 100);
           iY = i;
           jY = j;
         }
       }
     }

     System.out.println("x=" + maxX + "i,j" + "i=" + iX + "j=" + jX + "y=" + maxY + "i,j" + "i=" + iY + "j=" + jY);

     // color1 to color2
     Color color1 = new Color(255, 220, 234);
     Color color2 = new Color(105, 0, 58);

     // Normalize data
     double maxData = 0;
     double minData = heatMap[10][10];
     for (int i = 0; i < heatMap.length - 1; i++) {
       for (int j = 0; j < heatMap[0].length - 1; j++) {
         if (maxData < heatMap[i][j]) {
           maxData = heatMap[i][j];
         }
         if (heatMap[i][j] != 0 && minData > heatMap[i][j]) {
           minData = heatMap[i][j];
         }
       }
     }
     System.out.println("max=" + maxData + "min=" + minData);

     // Load image to buffer
     BufferedImage mapImage = null;
     try {
       mapImage = (ImageIO.read(new File(imagePath)));
     } catch (IOException e) {
       e.printStackTrace();
     }

     // Create image
     //TODO: Fit Map size to HeatMap size
  final BufferedImage image =
       new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
   final BufferedImage image =
           new BufferedImage(heatMap.length,heatMap[0].length, BufferedImage.TYPE_INT_ARGB);
     final BufferedImage image =
             new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_ARGB);
     final Graphics2D graphics2D = image.createGraphics();
     graphics2D.drawImage(mapImage, 0, 0, null);

     // Draw the map
     for (int i = 0; i < heatMap.length - 1; i++) {
       for (int j = 0; j < heatMap[0].length - 1; j++) {
         if (heatMap[i][j] == 0) {
           System.out.println("ERROR:" + "\n" + "heatmap matrix value should not be 0");
         }

         float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

         int r =
                 (int)
                         (((float) color2.getRed() - (float) color1.getRed()) * value
                                 + (float) color1.getRed());
         int g =
                 (int)
                         (((float) color2.getGreen() - (float) color1.getGreen()) * value
                                 + (float) color1.getGreen());
         int b =
                 (int)
                         (((float) color2.getBlue() - (float) color1.getBlue()) * value
                                 + (float) color1.getBlue());

         Color color = new Color(r, g, b, 50);
         graphics2D.setPaint(color);
         int x = 0;
         int y = 0;
         for (StopsAround stp : stopsAroundList) {
           if (stp.i == i && stp.j == j) {
             x =
             getXpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
             y = getYpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
             //System.out.println("i= "+i+"j= "+j);
             //if (x < image.getWidth() && y < image.getHeight()) {
             // System.out.println("Painted"+"x="+x+"y="+y);
             graphics2D.setPaint(Color.black);
             graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 28));
             graphics2D.drawString("X", x, y);
             // }
             break;
           }
         }
         graphics2D.setPaint(color);
         x =
         getXpixel((double) j / 100, (double) i / 100);
         y = getYpixel((double) j / 100, (double) i / 100);
         //if(x < image.getWidth() && y < image.getHeight())
         graphics2D.fillRect(x, y, 5, 5);

       }
     }
     graphics2D.dispose();
     // Save file
     boolean writeOnImage = false;
     try {
       writeOnImage =
               ImageIO.write(
                       image,
                       "png",
                       new File(
                               heatMapPath.get()));
     } catch (IOException e) {
       e.printStackTrace();
     }
     if (!writeOnImage) {
       System.out.println("can't write on image");
     }
     System.out.println("saved");
   }*/

  /*----Old functions x,y to long,lat-----

  //y = 5.347393957·10-7 x12 + 3.503290175·10-7 x1 x2 + 9.781328537·10-10 x22 - 5.586224394·10-4 x1
  // - 1.614915474·10-3 x2 + 32.34565226
  public double getLongitudeByXY ( int x, int y)
  {
    double longitude = 5.347393957 * Math.pow(10, -7) * Math.pow(x, 2) + 3.503290175 * Math.pow(10, -7) * x * y
            + 9.781328537 * Math.pow(10, -10) * Math.pow(y, 2) - 5.586224394 * Math.pow(10, -4) * x
            - 1.614915474 * Math.pow(10, -3) * y
            + 32.34565226;
    return longitude;

  }*/
  // y = -1.28461208·10-6 x12 - 1.39031381·10-6 x1 x2
  // - 3.786608865·10-7 x22 + 3.287492839·10-3 x1 + 9.395008917·10-4 x2 + 33.77106813
  /* public double getLatitudeByXY ( int x, int y)
  {
    double latitude = -1.28461208 * Math.pow(10, -6) * Math.pow(x, 2) - 1.39031381 * Math.pow(10, -6) * x * y
            - 3.786608865 * Math.pow(10, -7) * Math.pow(y, 2) + 3.287492839 * Math.pow(10, -3) * x
            + 9.395008917 * Math.pow(10, -4) * y + 33.77106813;
    return latitude;
  }*/
  private void arrivalTimeToLocationForMapNewFunction() {

    // TODO: Try lon,lat or lat,lon
    // Right high 32.053978 34.784839
    // Left high 32.756847, 33.650970
    // Right low 31.155445, 35.212540
    // Left low 31.224366, 33.928004
    double maxLongitude = 35.23; // Max Longitude point on map(in the borders)
    double maxLatitude = 32.75; // Max Latitude point on map(in the borders)

    // Location matrix *100 to make difference between points,*2 for two sides of the map
    heatMap = new double[770][918]; // TODO: Change to Map size and add Data to getX Y functions
    int x = 0;
    int y = 0;
    for (StopsAround stp : stopsAroundList) {

      // Arrival time of each stop from the chosen stop by location
      y = getYpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
      x = getXpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
      stp.i = x;
      stp.j = y;
      heatMap[x][y] = (stp.arrivalTime);
    }
    // else
    // System.out.println("x=" + x + "y=" + y);

    // Distance of other points to the nearest stop
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {

        if (heatMap[i][j] == 0) // not a stop
        {
          StopsAround firstStop = stopsAroundList.get(0);
          double ac = Math.abs(0 - firstStop.j); // y distance
          double cb = Math.abs(0 - firstStop.i); // x distance
          double distance = Math.hypot(ac, cb) * 60;
          double stopTimeArrival = firstStop.arrivalTime;

          // Find nearest stop
          for (StopsAround stp : stopsAroundList) {
            ac = Math.abs(i - stp.j); // y distance
            cb = Math.abs(j - stp.i); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb) * 60;
              stopTimeArrival = stp.arrivalTime;
            }
          }
          // *60 minutes to seconds
          heatMap[i][j] = ((distance) + stopTimeArrival);
        }
        if (heatMap[i][j] == 0) {
          System.out.println("Check heatMap assignment");
        }
      }
    }
  }

  private boolean inBorders(int x, int y) {
    // Not in the Sea area
    // y = -3.244698554·10-3 x2 - 5.987572519·10-1 x + 583.173052
    // y = -2.156465775·10-3 x2 - 5.951883509·10-1 x + 693.4643201
    if (y
        > -3.373851571 * (Math.pow(10, -3)) * (Math.pow(x, 2))
            - 1.457345683 * (Math.pow(10, -1)) * x
            + 657.4869216) {
      return true;
    }
    /*
      if(x>400)//Sea top
      {
        return true;
      }
      else
        {
        if (y>580 && y>0)//Sea bottom
        {
          return true;
        }
      else
        {
        if(x>300&& y>90)//Right to Tel-aviv
        {
          return true;
        }
        else{
          if(x>280 && y>250)//Right to Ashdod
            return true;
          else {
            if(x>180&&y>390)
              return true;
          }

          }
        }
    }*/
    return false;
  }

  public void drawMapNew(String imagePath) {

    // color1 to color2
    Color color1 = new Color(255, 208, 235);
    Color color2 = new Color(126, 0, 67);

    // Normalize data
    double maxData = 0;
    double minData = heatMap[10][10];
    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (maxData < heatMap[i][j]) {
          maxData = heatMap[i][j];
        }
        if (heatMap[i][j] != 0 && minData > heatMap[i][j]) {
          minData = heatMap[i][j];
        }
      }
    }

    // Load image to buffer
    BufferedImage mapImage = null;
    try {
      mapImage = (ImageIO.read(new File(imagePath)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("w=" + mapImage.getWidth() + "h=" + mapImage.getHeight());

    // Create image
    final BufferedImage image =
        new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();

    graphics2D.drawImage(mapImage, 0, 0, null);

    // Draw the map

    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (heatMap[i][j] == 0) {
          System.out.println("ERROR:" + "\n" + "heatmap matrix value should not be 0");
        }

        float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

        int r =
            (int)
                (((float) color2.getRed() - (float) color1.getRed()) * value
                    + (float) color1.getRed());
        int g =
            (int)
                (((float) color2.getGreen() - (float) color1.getGreen()) * value
                    + (float) color1.getGreen());
        int b =
            (int)
                (((float) color2.getBlue() - (float) color1.getBlue()) * value
                    + (float) color1.getBlue());

        Color color = new Color(r, g, b, 80);
        graphics2D.setPaint(color);
        if (inBorders(i, j)) {
          graphics2D.fillRect(i, j, 2, 2);
        }
      }
    }
    for (StopsAround stp : stopsAroundList) {
      graphics2D.setPaint(Color.black);
      graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 10));
      int x = getXpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
      int y = getYpixel(stp.stop.getStopLat(), stp.stop.getStopLon());
      System.out.println(
          stp.stop.getStopName()
              + " "
              + "lon="
              + stp.stop.getStopLon()
              + "lat="
              + stp.stop.getStopLat()
              + "x="
              + x
              + "y="
              + y);
      graphics2D.drawString("X", x, y);
    }

    // TODO:Each stop to its place by adding/decreasing to x,y
    graphics2D.drawString("Y", 330 + 80, 633 + 160); //
    graphics2D.dispose();
    // Save file
    boolean createImage = false;
    try {
     /* createImage =
          ImageIO.write(
              image,
              "png",
              new File(
                  heatMapPath.get()));
      */
      createImage =
              ImageIO.write(
                      image,
                      "png",
                      new File(
                              heatMapPath.get()));

    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!createImage) {
      System.out.println("can't create image");
    }
  }

  /*---Old draw method to draw heatmap on map with new functions: x,y to longitude,latitude---

  public void drawOnRealMapNewFunctions (String imagePath){

    // color1 to color2
    Color color1 = new Color(255, 220, 234);
    Color color2 = new Color(105, 0, 58);

    // Load image to buffer
    BufferedImage mapImage = null;
    try {
      mapImage = (ImageIO.read(new File(imagePath)));
    } catch (IOException e) {
      e.printStackTrace();
    }


    // Normalize data
    int maxData = 0;//heatMap[(int)maxLatitude][(int)maxlongitude];
    int minData = 0;
    int maxLatitude = 0;
    int maxlongitude = 0;
    // Create image
    //TODO: Fit Map size to HeatMap size
    final BufferedImage image =
            new BufferedImage((int) maxLatitude * 100, (int) maxlongitude * 100, BufferedImage.TYPE_INT_ARGB);
  //final BufferedImage image =
      //    new BufferedImage(heatMap.length,heatMap[0].length, BufferedImage.TYPE_INT_ARGB);
   // final Graphics2D graphics2D = image.createGraphics();
    graphics2D.drawImage(mapImage, 0, 0, null);

    // Draw the map
    for (int i = 0; i < image.getHeight() - 1; i++) {
      for (int j = 0; j < image.getWidth() - 1; j++) {

        double longitude = getLongitudeByXY(i, j);
        double latitude = getLatitudeByXY(i, j);
        if (longitude * 100 < heatMap.length - 1 && latitude * 100 < heatMap[0].length - 1) {
          float value = (float) (heatMap[(int) (longitude * 100) - 1][(int) (latitude * 100) - 1] - minData) / (float) (maxData - minData);
          int r =
                  (int)
                          (((float) color2.getRed() - (float) color1.getRed()) * value
                                  + (float) color1.getRed());
          int g =
                  (int)
                          (((float) color2.getGreen() - (float) color1.getGreen()) * value
                                  + (float) color1.getGreen());
          int b =
                  (int)
                          (((float) color2.getBlue() - (float) color1.getBlue()) * value
                                  + (float) color1.getBlue());
          if ((r > 0 && r < 255) && (g > 0 && g < 255) && (b > 0 && b < 255)) {
            System.out.println("r=" + r + "b=" + b + "g" + g);
            Color color = new Color(r, g, b, 50);
            graphics2D.setPaint(color);
            graphics2D.fillRect(i, j, 5, 5);
          }
        }
      }
    }
    //X on stops
      for (StopsAround stp : stopsAroundList) {
        if (stp.i == i && stp.j == j) {
          x =
          getXpixel(stp.stop.getStopLat(),stp.stop.getStopLon());
          y = getYpixel(stp.stop.getStopLat(),stp.stop.getStopLon());
          graphics2D.setPaint(Color.black);
          graphics2D.setFont(new Font("TimesRoman",Font.BOLD,28));
          graphics2D.drawString("X",x, y);
          // }
          break;
        }
      }

    graphics2D.dispose();
    // Save file
    boolean writeOnImage = false;
    try {
      writeOnImage =
              ImageIO.write(
                      image,
                      "png",
                      new File(
                              heatMapPath.get()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!writeOnImage) {
      System.out.println("can't write on image");
    }
    System.out.println("saved");
  }*/

  public static void main(String[] args) throws IOException {
    // TODO:
    //      Heatmap matrix size to max mapsize
    //      Boarders of israel,do not paint sea area
    //      Improve x,y pixel method by using more data
    //      Change i,j in StopsAround to x,y
    //      Fix latitude longitude names

    /** To use change output path on drawOnRealMap */
    Flags.parseCurrentPackage(args);
    StopTime stopTime;
    Trip trip;

    try (FileInputStream tripInputStream =
            new FileInputStream(new File("projects/opentrain/gtfs_pipeline/trips.protobin"));
        FileInputStream stopTimeInputStream =
            new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"))) {

      // Test some trip stops heatMap
      trip = Trip.parseDelimitedFrom(tripInputStream);
      stopTime = StopTime.parseDelimitedFrom(stopTimeInputStream);

      while (stopTime != null | trip != null) {
        if (trip.getTripId().equals(stopTime.getTripId())
            && Integer.parseInt(stopTime.getStopSequence()) == 1) {

          HeatMap heatmap = new HeatMap(stopTime.getStopId(), stopTime.getDepartureTime());

          // HeatMap color test

          // heatmap.arrivalTimeToLocation();
          // heatmap.drawMap(imagePath.get());

          // HeatMap on map test

          // Old methods
          // heatmap.arrivalTimeToLocationForMap();
          // heatmap.drawOnRealMap(imagePath.get());

          // heatmap.arrivalTimeToLocationForMapNewFunction();
          // heatmap.drawOnRealMapNewFunctions(imagePath.get());

          heatmap.arrivalTimeToLocationForMapNewFunction();
          heatmap.drawMapNew(imagePath.get());
          break;
        }
        stopTime = StopTime.parseDelimitedFrom(stopTimeInputStream);
      }
    }
  }
}

