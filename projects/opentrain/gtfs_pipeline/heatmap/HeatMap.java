package projects.opentrain.gtfs_pipeline.heatmap;

import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeatMap {


    public List<Stops_around> stopsAround = new ArrayList<>();

    public int[][] heatMap;

    public class Stops_around //the stop and his location on arrival_time matrix
    {

        int i = 0; //index on heatMap
        int j = 0;
        Stop stop;
        double arrivalTime;


        public Stops_around(StopTime stopT, double arrivalT) {

            arrivalTime = arrivalT;
            Stop s;
            FileInputStream StopInput = null;

            try {
                StopInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stops.protobin"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while (true) {//from all stops class pick my stop from stopsTime *to use lon/lat
                try {
                    s = Stop.parseDelimitedFrom(StopInput);

                    if (s == null)
                        break;
                    if (s.getStopId() == stopT.getStopId()) {
                        stop = s;
                        StopInput.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }


    public void arrival_time_to_location() {//by stops.lat,stops.lon make matrix of colors


        double max_lon = 0; // longitude
        double max_lat = 0; // latitude

        for (Stops_around stp : stopsAround) {
            if (stp.stop.getStopLat() > max_lat)
                max_lat = (stp.stop.getStopLat());


            if (stp.stop.getStopLon() > max_lon)
                max_lon = stp.stop.getStopLon();
        }


        //location matrix *100 to make difference between points
        heatMap = new int[(int) (max_lat * 100) + 1][(int) (max_lon * 100) + 1];


        for (Stops_around stp : stopsAround) {

            //arrival time of each stop from my stop by location
            stp.i = (int) (stp.stop.getStopLat() * 100);
            stp.j = (int) (stp.stop.getStopLon() * 100);
            heatMap[stp.i][stp.j] = (int) (stp.arrivalTime);
            //*100 to make difference between location

        }


        //distance of other points to the nearest stop

        for (int i = 0; i < heatMap.length - 1; i++) {
            for (int j = 0; j < heatMap[0].length - 1; j++) {



                if (heatMap[i][j] == 0)//not a stop
                {
                    Stops_around firstStop = stopsAround.get(0);
                    double ac = Math.abs(0 - firstStop.i); //y distance
                    double cb = Math.abs(0 - firstStop.j); //x distance
                    double distance = Math.hypot(ac, cb);
                    double stopTimeArrival = firstStop.arrivalTime;

                    for (Stops_around stp : stopsAround) { //find nearest stop

                        ac = Math.abs(i - stp.i); //y distance
                        cb = Math.abs(j - stp.j); //x distance

                        if (distance > Math.hypot(ac, cb)) {
                            distance = Math.hypot(ac, cb);
                            stopTimeArrival = stp.arrivalTime;
                        }
                    }


                       heatMap[i][j] = (int) ((distance * 60) + stopTimeArrival);
                    //*60 minutes to seconds
                }
                if (heatMap[i][j] == 0)
                    System.out.println("check");

            }
        }
    }


    // Constructor
    public HeatMap(int stop_id, String time) { // time = HH:MM:ss


        StopTime stopT;
        Trip trip;

        FileInputStream StopTimeInput = null;
        FileInputStream StopTimeInput2 = null;
        FileInputStream TripsInput = null;


        try {
            StopTimeInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"));
            StopTimeInput2 = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"));
            TripsInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/trips.protobin"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {


            while (true) //from all the stops
            {

                stopT = StopTime.parseDelimitedFrom(StopTimeInput);
                if (stopT == null)
                    break;

                if (stopT.getStopId() == stop_id                  //   pick the stop that i want
                        && Integer.parseInt(stopT.getStopSequence()) == 1 //that start some trip
                        && stopT.getDepartureTime().equals(time))  //at my time
                {
                    while (true) //from all the trips
                    {

                        trip = Trip.parseDelimitedFrom(TripsInput);
                        if (trip == null)
                            break;

                        if (stopT.getTripId().equals(trip.getTripId())) //pick the trip that starts on this stop
                        {
                            StopTime tripStops;
                            while (true) //from all the stops pick the stops on this trip
                            {

                                tripStops = StopTime.parseDelimitedFrom(StopTimeInput2);
                                if (tripStops == null)
                                    break;

                                if (tripStops.getTripId().equals(trip.getTripId()) //stops beside my stop
                                        && tripStops.getStopId() != stopT.getStopId()) {

                                    // ArrivalTime HH:mm:ss to double seconds
                                    String[] hhMMss = (tripStops.getArrivalTime().split(":"));
                                    Double arrivalT = Double.parseDouble(hhMMss[0]) * 3600 + //H
                                            Double.parseDouble(hhMMss[1]) * 60 //m
                                            + Double.parseDouble(hhMMss[2]); //s

                                    stopsAround.add(new Stops_around(tripStops, arrivalT));

                                }
                            }
                        }

                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                StopTimeInput.close();
                StopTimeInput2.close();
                TripsInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void Draw_map() {

        //color1 to color2
        Color c1 = new Color(255, 208, 235);
        Color c2 = new Color(126, 0, 67);

        //normalize data
        int maxData = 0;
        int minData = heatMap[10][10];
        for (int i = 0; i < heatMap.length - 1; i++) {
            for (int j = 0; j < heatMap[0].length - 1; j++) {
                if (maxData < heatMap[i][j])
                    maxData = heatMap[i][j];
                if (heatMap[i][j] != 0 && minData > heatMap[i][j])
                    minData = heatMap[i][j];
            }
        }
        System.out.println("max= " + maxData + "min" + minData);


        //create image

        final BufferedImage image = new BufferedImage(heatMap[0].length - 1000, heatMap.length - 1000, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = image.createGraphics();

        //draw the map

       for (int i = 0; i < heatMap.length-1; i++) {
            for (int j = 0; j < heatMap[0].length - 1; j++) {
                if(heatMap[i][j] == 0)
                    System.out.println("heatmap=0 error");

                float value = (float) (heatMap[i][j] - minData) / (float) (maxData - minData);

                int r=(int)(((float) c2.getRed() - (float) c1.getRed())*value+ (float) c1.getRed());
                int g=(int)(((float) c2.getGreen() - (float) c1.getGreen())*value + (float) c1.getGreen());
                int b=(int)(((float) c2.getBlue() - (float) c1.getBlue())*value+ (float) c1.getBlue());

                Color c = new Color(r,g,b);
                graphics2D.setPaint(c);
                graphics2D.fillRect(i, j, 2, 2);

            }
        }

        graphics2D.dispose();
       //save file
        boolean img = false;
        try {
            img = ImageIO.write(image, "png", new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/projects/opentrain/gtfs_pipeline/heatmap/imagePng.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(img == false)
            System.out.println("can't create img");

    }


    public static void main(String[] args) {

        StopTime stopT;
        Trip trip;

        FileInputStream StopTInput = null;
        FileInputStream tripInput = null;
        try {
            tripInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/trips.protobin"));
            StopTInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

            //test some trip stops heatMap
        try {
            trip = Trip.parseDelimitedFrom(tripInput);
            stopT = StopTime.parseDelimitedFrom(StopTInput);
            while (true) {
                if (stopT == null | trip == null)
                    break;

                if (trip.getTripId().equals(stopT.getTripId()) && Integer.parseInt(stopT.getStopSequence()) == 1) {
                    System.out.println("id=" + stopT.getStopId() + "seq=" + stopT.getStopSequence() + "trip id=" + trip.getTripId() + "\n");
                    HeatMap hm = new HeatMap(stopT.getStopId(), stopT.getDepartureTime());
                    hm.arrival_time_to_location();
                    hm.Draw_map();
                    break;

                }
                stopT = StopTime.parseDelimitedFrom(StopTInput);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




