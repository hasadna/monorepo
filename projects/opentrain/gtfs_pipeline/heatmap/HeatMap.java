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

import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

public class HeatMap { //**************each for need to use ParserDelimiterFrom

	//public double[][] arrival_time = new double[10][10];
	/**public Stops_around[] stopsAround = new Stops_around[10];**/
    public List<Stops_around> stopsAround = new ArrayList<>();

    public int[][] heatMap;

	public class Stops_around //the stop and his location on arrival_time matrix
	{
		public
		Stop stop;
		int i;
		int j;
		double arrivalTime;

        public Stops_around(StopTime stopT,double arrivalT)
        {
            arrivalTime = arrivalT;
            Stop s;
            FileInputStream StopInput = null;

            try {
                StopInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stops.p"));
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

        /******array constructor****/
        /*
		public Stops_around(StopTime stopT, int ii, int jj) {

			i = ii;
			j = jj;

			Stop s;
            FileInputStream StopInput = null;

            try {
                 StopInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stops.p"));
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

		int max_lon = 0; // longitude
		int max_lat = 0; // latitude

		for (Stops_around stp : stopsAround) {
			if (stp.stop.getStopLat() > max_lat)
				max_lat = (int)(stp.stop.getStopLat());

			if (stp.stop.getStopLon() > max_lon)
				max_lon = (int)stp.stop.getStopLon();
		}

		heatMap = new int[max_lat * 1000][(max_lon * 1000)]; //location matrix,  *1000 to make difference between points

		/**for (Stops_around stp : stopsAround) {
			//arrival time of each stop from my stop by location
			heatMap[(int)(stp.stop.getStopLat() * 1000)][(int)(stp.stop.getStopLon() * 1000)]
                    = (int)(10*arrival_time[stp.i][stp.j]);
		}
**/
        for (Stops_around stp : stopsAround) {
            //arrival time of each stop from my stop by location
            heatMap[(int)(stp.stop.getStopLat() * 1000)][(int)(stp.stop.getStopLon() * 1000)]
                    = (int)(10*stp.arrivalTime);
        }

	}


	// Constructor
	public HeatMap(int stop_id, String time) { // time = HH:MM:ss




        int k = 0;//number of all the stops around

        StopTime stopT;
        FileInputStream StopTimeInput = null;
        FileInputStream StopTimeInput2 = null;
        FileInputStream TripsInput = null;
        try {
            StopTimeInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stop_times.p"));
            StopTimeInput2 = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stop_times.p"));
            TripsInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/trips.p"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while (true) { //from all the stops
                stopT = StopTime.parseDelimitedFrom(StopTimeInput);
                if (stopT == null)
                    break;

                if (stopT.getStopId() ==  stop_id) {  // pick the stop that i want

                    int i = 0;//number of trip

                    Trip trip = null;
                    while (true)  ////from all the trips
                    {
                        int j = 0;//number of stops on trip

                        trip = Trip.parseDelimitedFrom(TripsInput);
                        if (trip == null)
                            break;

                        //pick the trips that starts on my stop at my time
                        if (stopT.getTripId() == trip.getTripId()
                                && Integer.parseInt(stopT.getStopSequence()) == 1 &&
                                /**Double.parseDouble(stopT.getDepartureTime()) == time**/
                                stopT.getDepartureTime().equals(time))
                        {

                            StopTime tripStops;
                            while (true) { //from all the stops pick the stops on this trip

                                tripStops = StopTime.parseDelimitedFrom(StopTimeInput2);
                                if (tripStops == null)
                                    break;

                                if (tripStops.getTripId() == trip.getTripId()
                                        && tripStops.getStopId() != stopT.getStopId())
                                {

                                    // ArrivalTime HH:mm:ss to double
                                    String [] hhMMss = (tripStops.getArrivalTime().split(":"));
                                    Double arrivalT = Double.parseDouble(hhMMss[0])+
                                            Double.parseDouble(hhMMss[1])/60
                                            +Double.parseDouble(hhMMss[2])/3600;

                                    /**arrival_time[i][j] = arrivalT;   try without array
                                    stopsAround[k++] = new Stops_around(tripStops, i, j); /***stopsAround to list**/
                                    stopsAround.add(new Stops_around(tripStops,arrivalT));
                                    j++; //next stop on this trip
                                }
                            }
                            i++; // next trip
                        }


                    }

                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
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

		public void Draw_map (){

		//create image
		final BufferedImage image = new BufferedImage(heatMap[0].length, heatMap.length, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = image.createGraphics();

		//draw the map

		for (int i = 0; i < heatMap[0].length; i++) {
			for (int j = 0; j < heatMap.length; j++) {
				Color c = new Color( 255-heatMap[i][j], 100-heatMap[i][j], 200-heatMap[i][j]);
				//graphics2D.fillRect ( i,j,1,1 );
				graphics2D.setPaint(c);
				graphics2D.drawLine(i, j, i, j);

			}
		}
		graphics2D.dispose();

		//save file
		try {
			ImageIO.write(image, "png", new File("image2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


		public static void main (String[] args){
    /**   All id=0 by protos.java in common_proto-speed-src.jar ****************/
		    StopTime stopT;
            Stop stop;
		    Trip trip;

            FileInputStream StopInput = null;
            FileInputStream StopTInput = null;
            FileInputStream tripInput = null;
            try {
                tripInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/trips.p"));
                StopInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stops.p"));
                StopTInput = new FileInputStream(new File("/home/rami/Desktop/googleCR/bazel-0.23.2-dist/hasadna/bazel-genfiles/projects/opentrain/gtfs_pipeline/stop_times.p"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            /*
            try {
                stop = StopTime.parseDelimitedFrom(StopInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(stop.getDepartureTime());
             //HeatMap hm = new HeatMap(stop.getStopId(),"00:00:00");

                System.out.println(hm.stopsAround+"\n");

            hm.arrival_time_to_location();
            //hm.Draw_map();
*/
            try {
               trip = Trip.parseDelimitedFrom(tripInput);
                while(true) {
                    stopT = StopTime.parseDelimitedFrom(StopTInput);
                    if(stopT == null | trip ==null )
                        break;
                        stopT = StopTime.parseDelimitedFrom(StopTInput);
                    System.out.println("id="+stopT.getStopId()+ "\n");

                    // if (trip.getTripId().equals(stop.getTripId())) {
                           // HeatMap hm = new HeatMap(stop.getStopId(), stop.getDepartstopureTime());
                           // System.out.println("trip size= "+"size = " + hm.stopsAround.size() + "\n");

                        //}

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


}




