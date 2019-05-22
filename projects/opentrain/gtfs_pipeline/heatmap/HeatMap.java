package projects.opentrain.gtfs_pipeline.heatmap;

import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

import com.google.startupos.common.flags.Flags;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeatMap {

  private List<StopsAround> stopsAroundList = new ArrayList<>();

  private double[][] heatMap;
  private Stop startStop;
  private List<Stop> stopsList = new ArrayList<>();
  private List<StopTime> stopTimeList = new ArrayList<>();
  private List<Trip> tripsList = new ArrayList<>();
  private static final String TRIPS_PATH = "projects/opentrain/gtfs_pipeline/trips.protobin";
  private static final String STOPS_PATH = "projects/opentrain/gtfs_pipeline/stops.protobin";
  private static final String STOP_TIME_PATH = "projects/opentrain/gtfs_pipeline/stopTime.protobin";
  private static final int WIDTH = 918; // Image map width
  private static final int HEIGHT = 770; // Image map height

  @FlagDesc(
      name = "image_path",
      description = "Image path to map image. Needs to contain .../<FileName>.png")
  public static Flag<String> imagePath = Flag.create("");

  @FlagDesc(
      name = "heat_map_path",
      description =
          "Image destination path to create heatmap image. Needs to contain .../<FileName>.png")
  public static Flag<String> heatMapPath = Flag.create("");

  public HeatMap() throws IOException {
    loadDataFromFiles();
  }

  public HeatMap(List<Stop> stopsList, List<StopTime> stopTimeList, List<Trip> tripsList) {
    this.stopsList.addAll(stopsList);
    this.stopTimeList.addAll(stopTimeList);
    this.tripsList.addAll(tripsList);
  }

  public void createHeatMap(int stopId, String time) throws IOException {
    /** time has format HH:MM:ss */
    for (Stop stop : stopsList) {
      if (stop.getStopId() == stopId) {
        startStop = stop;
      }
    }
    // From all the stops
    for (StopTime stopTime : stopTimeList) {
      // Pick the stop that we want, that starts at some trip, at the given time
      if (stopTime.getStopId() == stopId
          && Integer.parseInt(stopTime.getStopSequence()) == 1
          /** StopSequence = 1 this stop is the first one on the trip */
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
                    Double.parseDouble(hhMMss[0]) * 3600 // H
                        + Double.parseDouble(hhMMss[1]) * 60 // m
                        + Double.parseDouble(hhMMss[2]); // s

                stopsAroundList.add(new StopsAround(tripStops, arrivalTime));
              }
            }
          }
        }
      }
    }
  }

  private void loadDataFromFiles() throws IOException {
    try (FileInputStream tripsInputStream = new FileInputStream(new File(TRIPS_PATH));
        FileInputStream stopTimeInputStream = new FileInputStream(new File(STOP_TIME_PATH));
        FileInputStream stopInputStream = new FileInputStream(new File(STOPS_PATH))) {

      while (true) {
        Stop tmpStop = Stop.parseDelimitedFrom(stopInputStream);
        if (tmpStop == null) {
          break;
        }
        stopsList.add(tmpStop);
      }
      while (true) {
        StopTime tmpStopTime = StopTime.parseDelimitedFrom(stopTimeInputStream);
        if (tmpStopTime == null) {
          break;
        }
        stopTimeList.add(tmpStopTime);
      }
      while (true) {
        Trip tmpTrip = Trip.parseDelimitedFrom(tripsInputStream);
        if (tmpTrip == null) {
          break;
        }
        tripsList.add(tmpTrip);
      }
    }
  }

  // The stop and his location on arrival_time matrix
  public class StopsAround {

    // Index on heatMap or x,y on map
    int i = 0;
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

    for (StopsAround stopAround : stopsAroundList) {
      if (stopAround.stop.getStopLat() > maxLatitude) {
        maxLatitude = (stopAround.stop.getStopLat());
      }

      if (stopAround.stop.getStopLon() > maxLongitude) {
        maxLongitude = stopAround.stop.getStopLon();
      }
    }
    // maxLongitude = 35.23; //Max Longitude point on map(in the borders)
    // maxLatitude = 32.75; //Max Latitude point on map(in the borders)

    // Location matrix *100 to make difference between points
    // *2 to up and down, *2 right and left
    heatMap = new double[(int) (maxLatitude * 100) * 2][(int) (maxLongitude * 100) * 2];

    for (StopsAround stopAround : stopsAroundList) {

      // Arrival time of each stop from the chosen stop by location
      // *100 to make difference between locations
      stopAround.i = (int) (stopAround.stop.getStopLat() * 100);
      stopAround.j = (int) (stopAround.stop.getStopLon() * 100);
      heatMap[stopAround.i][stopAround.j] = (int) (stopAround.arrivalTime);
    }

    // Distance of other points to the nearest stop
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {

        if (heatMap[i][j] == 0) // not a stop
        {
          StopsAround firstStop = stopsAroundList.get(0);
          double ac = Math.abs(0 - firstStop.i); // y distance
          double cb = Math.abs(0 - firstStop.j); // x distance
          double distance = Math.hypot(ac, cb);
          double stopTimeArrival = firstStop.arrivalTime;

          // Find nearest stop
          for (StopsAround stopAround : stopsAroundList) {

            ac = Math.abs(i - stopAround.i); // y distance
            cb = Math.abs(j - stopAround.j); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb);
              stopTimeArrival = stopAround.arrivalTime;
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
  public void drawHeatMap(String imagePath) {
    // color1 to color2
    Color color1 = new Color(255, 208, 235);
    Color color2 = new Color(126, 0, 67);

    // Normalize data
    double maxData = 0;
    double minData = heatMap[0][0];
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {
        if (maxData < heatMap[i][j]) {
          maxData = heatMap[i][j];
        }
        /**
         * The chosen stop heatMap[i][j] = 0 and minimum data can't be 0, that won't affect on map
         * colors various
         */
        if (heatMap[i][j] != 0 && minData > heatMap[i][j]) {
          minData = heatMap[i][j];
        }
      }
    }

    // Create image
    final BufferedImage image =
        new BufferedImage(heatMap.length, heatMap[0].length, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();

    // Draw the map
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {
        if (heatMap[i][j] == 0) {
          System.out.println("ERROR:" + "\n" + "heatmap matrix value should not be 0");
        }

        float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

        int r = (int) (((float) color2.getRed() - color1.getRed()) * value + color1.getRed());
        int g = (int) (((float) color2.getGreen() - color1.getGreen()) * value + color1.getGreen());
        int b = (int) (((float) color2.getBlue() - color1.getBlue()) * value + color1.getBlue());

        Color color = new Color(r, g, b);
        graphics2D.setPaint(color);
        graphics2D.fillRect(i, j, 2, 2);
      }
    }
    // Draw "X" on stops location
    for (StopsAround stopAround : stopsAroundList) {
      graphics2D.setPaint(Color.black);
      graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 20));
      graphics2D.drawString("X", stopAround.i - 1, stopAround.j - 1);
    }

    graphics2D.dispose();
    // Save file
    boolean createImage = false;
    try {
      createImage = ImageIO.write(image, "png", new File(heatMapPath.get()));

    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!createImage) {
      System.out.println("can't create image");
    }
  }

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

    return xPixel + 58;
  }

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

    return yPixel + 53;
  }

  private void arrivalTimeToLocationForMap() {
    heatMap = new double[HEIGHT][WIDTH];

    // Start stop x,y
    int x = getXpixel(startStop.getStopLat(), startStop.getStopLon());
    int y = getYpixel(startStop.getStopLat(), startStop.getStopLon());
    int startStopX = x;
    int startStopY = y;
    heatMap[x][y] = 0;

    for (StopsAround stopAround : stopsAroundList) {

      // Arrival time of each stop from the chosen stop by location
      x = getXpixel(stopAround.stop.getStopLat(), stopAround.stop.getStopLon());
      y = getYpixel(stopAround.stop.getStopLat(), stopAround.stop.getStopLon());
      stopAround.i = x;
      stopAround.j = y;
      heatMap[x][y] = (stopAround.arrivalTime);
    }

    // Distance of other points to the nearest stop
    // *60 minutes to seconds
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {

        if (heatMap[i][j] == 0) // not a stop
        {
          // Distance from start stop
          double ac = Math.abs(i - startStopY); // y distance
          double cb = Math.abs(j - startStopX); // x distance
          double distance = Math.hypot(ac, cb) * 60;
          double stopTimeArrival = distance;

          if ((i == startStopX || j == startStopY)) {
            StopsAround firstStop = stopsAroundList.get(0);
            ac = Math.abs(i - firstStop.j); // y distance
            cb = Math.abs(j - firstStop.i); // x distance
            distance = Math.hypot(ac, cb) * 60;
            stopTimeArrival = firstStop.arrivalTime;
          }

          // Find nearest stop
          for (StopsAround stopAround : stopsAroundList) {
            ac = Math.abs(i - stopAround.j); // y distance
            cb = Math.abs(j - stopAround.i); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb) * 60;
              stopTimeArrival = stopAround.arrivalTime;
            }
          }

          heatMap[i][j] = (distance + stopTimeArrival);
        }
        if (heatMap[i][j] == 0 && (j != startStopX && i != startStopY)) {
          System.out.println("Check heatMap assignment");
          System.out.println("i= " + i + "j=" + j);
        }
      }
    }
  }

  private boolean inBorders(int x, int y) {
    // Not in the Sea area
    // y = -3.244698554·10-3 x2 - 5.987572519·10-1 x + 583.173052
    if (y
        > -3.373851571 * (Math.pow(10, -3)) * (Math.pow(x, 2))
            - 1.457345683 * (Math.pow(10, -1)) * x
            + 657.4869216) {
      return true;
    }
    return false;
  }

  public void drawHeatMapOnMap(String imagePath) {
    // color1 to color2
    Color color1 = new Color(255, 208, 235);
    Color color2 = new Color(126, 0, 67);

    // Normalize data
    double maxData = 0;
    double minData = heatMap[10][10];
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {
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

    // Create image
    final BufferedImage image =
        new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();

    graphics2D.drawImage(mapImage, 0, 0, null);

    // Draw the map
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[0].length; j++) {
        if (heatMap[i][j] == 0) {
          int x = getXpixel(startStop.getStopLat(), startStop.getStopLon());
          int y = getYpixel(startStop.getStopLat(), startStop.getStopLon());
          int startStopX = x;
          int startStopY = y;
          if (j != x && i != y) // Not the start stop
          {
            System.out.println("ERROR:" + "\n" + "heatmap matrix value should not be 0");
          }
        } else {
          float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

          int r = (int) (((float) color2.getRed() - color1.getRed()) * value + color1.getRed());
          int g =
              (int) (((float) color2.getGreen() - color1.getGreen()) * value + color1.getGreen());
          int b = (int) (((float) color2.getBlue() - color1.getBlue()) * value + color1.getBlue());

          Color color = new Color(r, g, b, 80);
          graphics2D.setPaint(color);
          if (inBorders(i, j)) {
            graphics2D.fillRect(i, j, 2, 2);
          }
        }
      }
    }
    for (StopsAround stopAround : stopsAroundList) {
      graphics2D.setPaint(Color.black);
      graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 10));
      int x = getXpixel(stopAround.stop.getStopLat(), stopAround.stop.getStopLon());
      int y = getYpixel(stopAround.stop.getStopLat(), stopAround.stop.getStopLon());

      graphics2D.drawString("X", x, y);
    }
    for (Stop stop : stopsList) {
      if (stop.getStopId() == startStop.getStopId()) {
        graphics2D.setPaint(Color.black);
        graphics2D.setFont(new Font("TimesRoman", Font.BOLD, 10));
        int x = getXpixel(stop.getStopLat(), stop.getStopLon());
        int y = getYpixel(stop.getStopLat(), stop.getStopLon());

        graphics2D.drawString("S", x, y);
      }
    }

    // TODO:Each stop to its place by adding/decreasing to x,y
    graphics2D.drawString("Y", 330 + 80, 633 + 160);
    graphics2D.dispose();
    // Save file
    boolean createImage = false;
    try {
      createImage = ImageIO.write(image, "png", new File(heatMapPath.get()));

    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!createImage) {
      System.out.println("can't create image");
    }
  }

  public static void main(String[] args) throws IOException {
    // TODO:
    //      Boarders of israel, do not paint sea area
    //      Improve x,y pixel method by using more data

    Flags.parseCurrentPackage(args);

    // Take the first trip in file, for testing:

    HeatMap heatMap = new HeatMap();
    Trip tripTest = heatMap.tripsList.get(0);
    StopTime stopTimeTest = heatMap.stopTimeList.get(0);
    if (tripTest == null) {
      System.out.println("Error: trips list is empty");
      System.exit(1);
    }
    if (stopTimeTest == null) {
      System.out.println("Error: stopTime list is empty");
      System.exit(1);
    }
    Boolean createHeatMap = false;
    for (Trip trip : heatMap.tripsList) {
      for (StopTime stopTime : heatMap.stopTimeList) {
        if (trip.getTripId().equals(stopTime.getTripId())
            && Integer.parseInt(stopTime.getStopSequence()) == 1) {
          /** StopSequence = 1, this stop is the first one on the trip */
          heatMap.createHeatMap(stopTime.getStopId(), stopTime.getDepartureTime());

          // ---HeatMap color test---

          // heatMap.arrivalTimeToLocation();
          // heatMap.drawHeatMap(imagePath.get());

          // ---HeatMap on map test---

          heatMap.arrivalTimeToLocationForMap();
          heatMap.drawHeatMapOnMap(imagePath.get());
          createHeatMap = true;
          break;
        }
      }
      if (createHeatMap) {
        break;
      }
    }
  }
}

