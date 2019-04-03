package projects.opentrain.gtfs_pipeline.heatmap;

import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

import com.google.startupos.common.flags.Flags;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HeatMap {

  public List<StopsAround> stopsAroundList = new ArrayList<>();

  public int[][] heatMap;

  @FlagDesc(
      name = "image_path",
      description = "Image path to create heatmap image,need to contain .../imageName.png")
  public static Flag<String> imagePath = Flag.create("");
  // The stop and his location on arrival_time matrix
  public class StopsAround {

    int i = 0; // Index on heatMap
    int j = 0;
    Stop stop;
    double arrivalTime;

    public StopsAround(StopTime stopTime, double arrivalTime) throws IOException {

      this.arrivalTime = arrivalTime;
      Stop stopAround;

      try (FileInputStream StopInputStream =
          new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stops.protobin"))) {

        while (true) { // From all stops class pick my stop from stopsTime *to use lon/lat

          stopAround = Stop.parseDelimitedFrom(StopInputStream);

          if (stopAround == null) {
            break;
          }
          if (stopAround.getStopId() == stopTime.getStopId()) {
            stop = stopAround;
            StopInputStream.close();
            break;
          }
        }
      }
    }
  }

  private void ArrivalTimeToLocation() { // by stops.lat,stops.lon make matrix of colors

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

    // Location matrix *100 to make difference between points
    heatMap = new int[(int) (maxLatitude * 100) + 1][(int) (maxLongitude * 100) + 1];

    for (StopsAround stp : stopsAroundList) {

      // Arrival time of each stop from my stop by location
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

          for (StopsAround stp : stopsAroundList) { // Find nearest stop

            ac = Math.abs(i - stp.i); // y distance
            cb = Math.abs(j - stp.j); // x distance

            if (distance > Math.hypot(ac, cb)) {
              distance = Math.hypot(ac, cb);
              stopTimeArrival = stp.arrivalTime;
            }
          }
          heatMap[i][j] = (int) ((distance * 60) + stopTimeArrival);
          // *60 minutes to seconds
        }
        if (heatMap[i][j] == 0) System.out.println("check");
      }
    }
  }

  public HeatMap(int stop_id, String time) throws IOException { // Time = HH:MM:ss

    StopTime stopTime;
    Trip trip;

    try (FileInputStream tripsInputStream =
            new FileInputStream(new File("projects/opentrain/gtfs_pipeline/trips.protobin"));
        FileInputStream stopTimeInputStream =
            new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"));
        FileInputStream stopTimeInputStream2 =
            new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"))) {

      while (true) // From all the stops
      {

        stopTime = StopTime.parseDelimitedFrom(stopTimeInputStream);
        if (stopTime == null) break;

        if (stopTime.getStopId() == stop_id //   Pick the stop that i want
            && Integer.parseInt(stopTime.getStopSequence()) == 1 // That start some trip
            && stopTime.getDepartureTime().equals(time)) // At my time
        {
          while (true) // From all the trips
          {

            trip = Trip.parseDelimitedFrom(tripsInputStream);
            if (trip == null) break;

            if (stopTime
                .getTripId()
                .equals(trip.getTripId())) // Pick the trip that starts on this stop
            {
              StopTime tripStops;
              while (true) // From all the stops pick the stops on this trip
              {
                tripStops = StopTime.parseDelimitedFrom(stopTimeInputStream2);
                if (tripStops == null) break;

                if (tripStops.getTripId().equals(trip.getTripId()) // Stops beside my stop
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
  }

  public void Draw_map() {

    // color1 to color2
    Color c1 = new Color(255, 208, 235);
    Color c2 = new Color(126, 0, 67);

    // Normalize data
    int maxData = 0;
    int minData = heatMap[10][10];
    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (maxData < heatMap[i][j]) maxData = heatMap[i][j];
        if (heatMap[i][j] != 0 && minData > heatMap[i][j]) minData = heatMap[i][j];
      }
    }

    // Create image
    /** TODO: Try different size of image */
    final BufferedImage image =
        new BufferedImage(
            heatMap[0].length - 1000, heatMap.length - 1000, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D graphics2D = image.createGraphics();

    // Draw the map
    for (int i = 0; i < heatMap.length - 1; i++) {
      for (int j = 0; j < heatMap[0].length - 1; j++) {
        if (heatMap[i][j] == 0) System.out.println("heatmap=0 error");

        float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

        int r = (int) (((float) c2.getRed() - (float) c1.getRed()) * value + (float) c1.getRed());
        int g =
            (int) (((float) c2.getGreen() - (float) c1.getGreen()) * value + (float) c1.getGreen());
        int b =
            (int) (((float) c2.getBlue() - (float) c1.getBlue()) * value + (float) c1.getBlue());

        Color c = new Color(r, g, b);
        graphics2D.setPaint(c);
        graphics2D.fillRect(i, j, 2, 2);
      }
    }

    graphics2D.dispose();
    // Save file
    boolean createImage = false;
    try {
      createImage = ImageIO.write(image, "png", new File(imagePath.get()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!createImage) {
      System.out.println("can't create image");
    }
  }

  public static void main(String[] args) throws IOException {

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
      while (true) {
        if (stopTime == null | trip == null) {
          break;
        }

        if (trip.getTripId().equals(stopTime.getTripId())
            && Integer.parseInt(stopTime.getStopSequence()) == 1) {
          HeatMap hm = new HeatMap(stopTime.getStopId(), stopTime.getDepartureTime());
          hm.ArrivalTimeToLocation();
          hm.Draw_map();
          break;
        }
        stopTime = StopTime.parseDelimitedFrom(stopTimeInputStream);
      }
    }
  }
}

