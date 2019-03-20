package projects.opentrain.gtfs_pipeline.heatmap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeatMap { //**************each for need to use ParserDelimiterFrom

	public int[][] arrival_time = new int[10][10];
	public Stops_around[] stopsAround = new Stops_around[10];
	public int[][] heatMap;

	public class Stops_around //the stop and his location on arrival_time matrix
	{
		public
		Stops stop;
		int i;
		int j;

		public Stops_around(Stop_times _stop, int ii, int jj) {

			i = ii;
			j = jj;

			for (Stops s : stops) //from all stops class pick my stop from stops_times *to use lon/lat
			{
				if (s.stop_id == _stop.stop_id)
					stop = s;
			}
		}

	}


	public void arrival_time_to_location() {
		//by stops.lat,stops.lon make matrix of colors
		int max_lon = 0; // longitude
		int max_lat = 0; // latitude

		for (Stops_around stp : stopsAround) {
			if (stp.stop.lat > max_lat)
				max_lat = stp.stop.lat;

			if (stp.stop.lon > max_lon)
				max_lon = stp.stop.lon;
		}

		heatMap = new int[max_lat * 100][(max_lon * 100)]; //location matrix,  *100 to make difference between points

		for (Stops_around stp : stopsAround) {
			//arrival time of each stop from my stop by location
			heatMap[(stp.stop.lat * 100).intValue][(stp.stop.lon * 100).intValue] = arrival_time[stp.i][stp.j];
		}

	}

	// Constructor
	public HeatMap(int stop_id, int time) {
		int k = 0;//number of all the stops around

		for (Stop_times stop : stop_times) //from all the stops
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


		public void Draw_map ( int[][] heatMap2){

		int[][] heatMap = heatMap2;

		//create image

		final BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = image.createGraphics();

		//draw the map
		for (int i = 0; i < heatMap[0].length; i++) {
			for (int j = 0; j < heatMap.length; j++) {
				Color c = new Color(i * heatMap[i][j], +i * heatMap[i][j], 50 + j * heatMap[i][j]);
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


	}


}




