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
  public static void main(String[] args) throws IOException {
    // args[7] is stops.txt
      Stops.Builder allStopsBuilder = Stops.newBuilder();
      Path stops_txt = Paths.get(args[7]);
      Path outputPath = Paths.get(args[args.length - 1]);
      CSVParser parser = CSVParser.parse(stops_txt, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      //remove the first record from the list
       csvRecords.remove(csvRecords.get(0));

       //iterate each record, create a message with builder
       for (CSVRecord record : csvRecords) {
         allStopsBuilder.addStops(Stop.newBuilder()
                 .setStopId(Integer.parseInt(record.get(0)))
                 .setStopCode(Integer.parseInt(record.get(1)))
                 .setStopName(record.get(2))
                 .setStopDesc(record.get(3))
                 .setStopLat(Double.parseDouble(record.get(4)))
                 .setStopLon(Double.parseDouble(record.get(5)))
                 .setLocationType(Integer.parseInt(record.get(6)))
                 .setParentStation(record.get(7))
                 .setZoneId(Integer.parseInt(record.get(8)))
                 .build());
       }
      Files.write(outputPath, TextFormat.printToString(allStopsBuilder).getBytes());
  }
}
