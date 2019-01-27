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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.lang.String;
import static java.nio.charset.StandardCharsets.UTF_8;
class DataHandler {
  
    //method "getStops" for protobuf the file stops.txt  
    public static Stops.Builder getStops(Path csvPath) {
    Stops.Builder allStopsBuilder = Stops.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      //remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      
      //iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        /*System.out.println(record.get(0));
        System.out.println(record.get(1));
        System.out.println(record.get(2));
        System.out.println(record.get(3));
        System.out.println(record.get(4));
        System.out.println(record.get(5));
        System.out.println(record.get(6));
        System.out.println(record.get(7));
        System.out.println(record.get(8));*/
        allStopsBuilder.addStops(Stop.newBuilder()
                .setStopId(Integer.parseInt(record.get(0)))
                .setStopCode(Integer.parseInt(record.get(1)))
                .setStopName(Integer.parseInt(record.get(2)))
                .setStopDesc(record.get(3))
                .setStopLat(Double.parseDouble(record.get(4)))
                .setStopLon(Double.parseDouble(record.get(5)))
                .setLocationType(Integer.parseInt(record.get(6)))
                .setParentStation(Integer.parseInt(record.get(7)))
                .setZoneId(Integer.parseInt(record.get(8)))
                .build());
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return allStopsBuilder;
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
