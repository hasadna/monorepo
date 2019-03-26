package projects.opentrain.gtfs_pipeline.heatmap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.projects.opentrain.gtfs_pipeline.Protos.Calendar;
import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

public class HeatMap { //**************each for need to use ParserDelimiterFrom

    //public double[][] arrival_time = new double[10][10];
    /**
     * public Stops_around[] stopsAround = new Stops_around[10];
     **/
    public List<Stops_around> stopsAround = new ArrayList<>();

    public int[][] heatMap;

    public class Stops_around //the stop and his location on arrival_time matrix
    {
        public
        Stop stop;
        int i;
        int j;
        double arrivalTime;

        public Stops_around(StopTime stopT, double arrivalT) {
            arrivalTime = arrivalT;
            Stop s;
            FileInputStream StopInput = null;

            try {
                StopInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stops.p"));
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

        /******array constructor****/
        /*
		public Stops_around(StopTime stopT, int ii, int jj) {

			i = ii;
			j = jj;

			Stop s;
            FileInputStream StopInput = null;

            try {
                 StopInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stops.p"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            while(true) {//from all stops class pick my stop from stopsTime *to use lon/lat
                try {
                    s = Stop.parseDelimitedFrom(StopInput);

                if(s == null)
                    break;
                if(s.getStopId() == stopT.getStopId()) {
                    stop = s;
                    StopInput.close();
                    break;
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

		}
*/
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

        heatMap = new int[(int)(max_lat*100)+1][(int)(max_lon*100)+1]; //location matrix *100 to make difference between points

        /**for (Stops_around stp : stopsAround) {
         //arrival time of each stop from my stop by location
         heatMap[(int)(stp.stop.getStopLat() * 1000)][(int)(stp.stop.getStopLon() * 1000)]
         = (int)(10*arrival_time[stp.i][stp.j]);
         }
         **/

        for (Stops_around stp : stopsAround) {

            //arrival time of each stop from my stop by location
            heatMap[(int)(stp.stop.getStopLat() * 100)][(int) (stp.stop.getStopLon() * 100)]
                    = (int)(10 * stp.arrivalTime);

        }



    }


    // Constructor
    public HeatMap(int stop_id, String time) { // time = HH:MM:ss


        int k = 0;//number of all the stops around

        StopTime stopT;
        Trip trip;

        FileInputStream StopTimeInput = null;
        FileInputStream StopTimeInput2 = null;
        FileInputStream TripsInput = null;

        String StopTimePath = "projects/opentrain/gtfs_pipeline/stopTime.protobin";
        String TripsPath = "projects/opentrain/gtfs_pipeline/trips.protobin";

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

                                     // ArrivalTime HH:mm:ss to double hours
                                     String[] hhMMss = (tripStops.getArrivalTime().split(":"));
                                     Double arrivalT = Double.parseDouble(hhMMss[0]) + //H
                                             Double.parseDouble(hhMMss[1]) / 60 //m
                                             + Double.parseDouble(hhMMss[2]) / 3600; //s

                                     //arrival_time[i][j] = arrivalT;   try without array
                                     //stopsAround[k++] = new Stops_around(tripStops, i, j); //stopsAround to list
                                     stopsAround.add(new Stops_around(tripStops, arrivalT));

                                 }
                             }
                         }

                     }
                 }
             }


            /******************old***********
            while (true) { //from all the stops
                stopT = StopTime.parseDelimitedFrom(StopTimeInput);
                if (stopT == null)
                    break;

                if (stopT.getStopId() == stop_id) {  // pick the stop that i want

                    int i = 0;//number of trip

                    Trip trip = null;
                    while (true)  ////from all the trips
                    {
                        int j = 0;//number of stops on trip

                        trip = Trip.parseDelimitedFrom(TripsInput);
                        if(trip.getTripId().equals("35597962_260119"))
                            System.out.println("trip found");
                        if (trip == null)
                            break;

                        //pick the trips that starts on my stop at my time
                        if (stopT.getTripId().equals(trip.getTripId())
                                && (Integer.parseInt(stopT.getStopSequence()) == 1) &&
                                (stopT.getDepartureTime().equals(time)))
                        {

                            StopTime tripStops;
                            while (true) { //from all the stops pick the stops on this trip

                                tripStops = StopTime.parseDelimitedFrom(StopTimeInput2);
                                if (tripStops == null)
                                    break;

                                if (tripStops.getTripId() == trip.getTripId()
                                        && tripStops.getStopId() != stopT.getStopId()) {

                                    // ArrivalTime HH:mm:ss to double
                                    String[] hhMMss = (tripStops.getArrivalTime().split(":"));
                                    Double arrivalT = Double.parseDouble(hhMMss[0]) +
                                            Double.parseDouble(hhMMss[1]) / 60
                                            + Double.parseDouble(hhMMss[2]) / 3600;

                                    //arrival_time[i][j] = arrivalT;   try without array
                                     //stopsAround[k++] = new Stops_around(tripStops, i, j); //stopsAround to list
                                    stopsAround.add(new Stops_around(tripStops, arrivalT));
                                    j++; //next stop on this trip
                                }
                            }
                            i++; // next trip
                        }


                    }


                }


            }**/

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

    /******for ****/
/*
        for (StopTime stop : stop_times) //from all the stops
		{
			if (stop.stop_id == stop_id)// pick the stop that i want
			{
				int i = 0;//number of trip

				for (Trip trip : trips)//from all the trips
				{
					int j = 0;//number of stops on trip

					if (stop.trip_id == trip.trip_id && stop.stop_sequence == 1 && stop.departure_time = time)//pick the trips that starts on my stop at my time
					{
						for (Stop_times trip_stops : stop_times)//from all the stops pick the stops on this trip
						{
							if (trip_stops.trip_id == trip.trip_id && trip_stops.stop_id != stop.stop_id) {
								arrival_time[i][j] = trip_stops.arrival_time;
								stopsAround[k++] = new Stops_around(trip_stops, i, j);
								j++;
							}

						}
						i++;
					}
				}

			}


		}


	}


*/
    public void Draw_map() {

        //create image
        final BufferedImage image = new BufferedImage(heatMap[0].length-1, heatMap.length-1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = image.createGraphics();

        //draw the map

        for (int i = 0; i < heatMap.length-1; i++) {
            for (int j = 0; j < heatMap[0].length-1; j++) {
                Color c=null;
                //c = new Color(236, 240, 255);
                //if(heatMap[i][j] != 0)
                    c = new Color(201 ,160 - heatMap[i][j]*2, 225 - heatMap[i][j]*3);
                graphics2D.setPaint(c);
                //graphics2D.fillRect (i,j,100,100 );
                graphics2D.drawLine(i, j, i, j);
                if(heatMap[i][j] != 0)
                System.out.println("At="+heatMap[i][j]+"i="+i+"j="+j);

            }
        }
        graphics2D.dispose();

        //save file
        try {
            ImageIO.write(image, "png", new File("projects/opentrain/gtfs_pipeline/heatmap/imagePng.png"));
            ImageIO.write(image, "jpeg", new File("projects/opentrain/gtfs_pipeline/heatmap/image.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        /**   All id=0 by protos.java in common_proto-speed-src.jar ****************/
        StopTime stopT;
        Stop stop;
        Trip trip;

        FileInputStream StopInput = null;
        FileInputStream StopTInput = null;
        FileInputStream tripInput = null;
        try {
            tripInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/trips.protobin"));
            StopInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stops.protobin"));
            StopTInput = new FileInputStream(new File("projects/opentrain/gtfs_pipeline/stopTime.protobin"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            trip = Trip.parseDelimitedFrom(tripInput);
            stopT = StopTime.parseDelimitedFrom(StopTInput);
            while (true)
            {
                if (stopT == null | trip == null)
                    break;

                if (trip.getTripId().equals(stopT.getTripId()) && Integer.parseInt(stopT.getStopSequence())==1)
                {
                    System.out.println("id=" + stopT.getStopId() +"seq="+stopT.getStopSequence()+"trip id="+trip.getTripId()+ "\n");
                    HeatMap hm = new HeatMap(stopT.getStopId(), stopT.getDepartureTime());
                    // System.out.println("size = " + hm.stopsAround.size() + "\n");
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




