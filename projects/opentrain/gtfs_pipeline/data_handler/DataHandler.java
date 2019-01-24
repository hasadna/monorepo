package projects.opentrain.gtfs_pipeline.data_handler;

//imports for proto
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stop;
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stops;
import com.google.protobuf.TextFormat;
import java.io.Reader;

//imports for Appache
import org.apache.commons.csv.CSVFormat;
import org. apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
    
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.lang.String;

import static java.nio.charset.StandardCharsets.UTF_8;

class DataHandler {
  private enum Columns {
    STOP_ID("Stop id"),
    STOP_CODE("Stop code"),
    STOP_NAME("Stopname"),
    STOP_DESC("Stop desc"),
    STOP_LAT("Stop lat"),
    STOP_LON("Stop lon"),
    LOCATION_TYPE("Location type"),
    PARENT_STATION("Parent station"),
    ZONE_ID("Zone id");

    // The CSV column name
    private final String name;

    Columns(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static Stops.Builder getStops(String csvPath) {
    Stops.Builder allStopsBuilder = Stops.newBuilder();
    try {
      CSVParser parser =
          new CSVParser(
              new InputStreamReader(new FileInputStream(csvPath), UTF_8),
              CSVFormat.DEFAULT.withHeader());

      for (CSVRecord record : parser) {
        int stopId = parseInt((record.get(Columns.STOP_ID.getName())));
        int stopCode = parseInt(record.get(Columns.STOP_CODE.getName()));
        int stopName = parseInt(record.get(Columns.STOP_NAME.getName()));
        String stopDesc = record.get(Columns.STOP_DESC.getName());
        double stopLat = parseDouble(record.get(Columns.STOP_LAT.getName()));
        double stopLon = parseDouble(record.get(Columns.STOP_LON.getName()));
        int locationType = parseInt(record.get(Columns.LOCATION_TYPE.getName()));
        int parentStation = parseInt(record.get(Columns.PARENT_STATION.getName()));
        int zoneId = parseInt(record.get(Columns.ZONE_ID.getName()));

        Stop stop =
            Stop.newBuilder()
                .setStopId(stopId)
                .setStopCode(stopCode)
                .setStopName(stopName)
                .setStopDesc(stopDesc)
                .setStopLat(stopLat)
                .setStopLon(stopLon)
                .setLocationType(locationType)
                .setParentStation(parentStation)
                .setZoneId(zoneId)
                .build();

                allStopsBuilder.addStops(stop);
      }
    } catch (IOException | ParseException e) {
      throw new RuntimeException(e);
    }
    return allStopsBuilder;
  }

  private static double parseDouble(String value) {
    return Double.parseDouble(value);
  }

  private static int parseInt(String value) {
    return Integer.parseInt(value);
  }


      public static void main(String[] args) throws IOException {
        
        //args[7] is stops.txt
        Path stops_txt = Paths.get(args[7]);




        //String output = "";
        Path outputPath = Paths.get(args[args.length-1]);
        //for (String path : args) {
            // TODO: Read the files in a streaming manner since they are large
           // Path curPath = Paths.get(path);
           // output += curPath.getFileName() + " lines: ";
            Files.write(outputPath, TextFormat.printToString(getStops(stops_txt.toString())).getBytes());
       // }
       // System.out.print(output);
    }
}
