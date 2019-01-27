package projects.opentrain.gtfs_pipeline.data_handler;

//imports for proto
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stop;
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stops;
import com.google.protobuf.TextFormat;
import java.io.Reader;

//imports for Appache
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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
    STOP_ID("stop_id"),
    STOP_CODE("stop_code"),
    STOP_NAME("stopname"),
    STOP_DESC("stop_desc"),
    STOP_LAT("stop_lat"),
    STOP_LON("stop_lon"),
    LOCATION_TYPE("location_type"),
    PARENT_STATION("parent_station"),
    ZONE_ID("zone_id");

    // The CSV column name
    private final String name;

    Columns(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static Stops.Builder getStops(Path csvPath) {
    Stops.Builder allStopsBuilder = Stops.newBuilder();
    try {
      Reader reader = Files.newBufferedReader(csvPath);
      CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();
      csvRecords.remove(csvRecords.get(0));
      for (CSVRecord record : csvRecords) {
        System.out.println(record.get(0));
        System.out.println(record.get(1));
        System.out.println(record.get(2));
        System.out.println(record.get(3));
        System.out.println(record.get(4));
        System.out.println(record.get(5));
        System.out.println(record.get(6));
        System.out.println(record.get(7));
        System.out.println(record.get(8));

        /*allStopsBuilder.addStops(
            Stop.newBuilder()
                .setStopId(parseInt(record.get(0)))
                .setStopCode(parseInt(record.get(1)))
                .setStopName(parseInt(record.get(2)))
                .setStopDesc(record.get(3))
                .setStopLat(parseDouble(record.get(4)))
                .setStopLon(parseDouble(record.get(5)))
                .setLocationType(parseInt(record.get(6)))
                .setParentStation(parseInt(record.get(7)))
                .setZoneId(parseInt(record.get(8)))
                .build());*/

      }

    } catch (IOException e) {
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
           Files.write(outputPath, TextFormat.printToString(getStops(stops_txt)).getBytes());
           // }
       // System.out.print(output);
    }
}
