package projects.opentrain.israel_railways_pipeline;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.projects.opentrain.israel_railway_pipeline;
import com.google.common.collect.ImmutableList;

public class DataHandler {

  private static final String ISRAEL_RAILWAYS_AGENCY_ID = "2";

  public static List<Routes> getRoutes(Path csvPath) {

    List<Routes> routes = new ArrayList<>();
    try {
      CSVParser parser =
              CSVParser.parse(
                      csvPath,
                      StandardCharsets.UTF_8,
                      CSVFormat.DEFAULT.withHeader(
                              "id",
                              "stop_ids"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }

        Routes.Builder route = Routes.newBuilder();
        routes.add(
                route
                        .setId(Integer.parseInt(record.get("id")))
                        .addAllStopIds(parseStopIds(record.get("stop_ids")))
                        .build());
      }
      return routes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImmutableList<Integer> parseStopIds(String stopIds) {
    ImmutableList.Builder<Integer> result = ImmutableList.builder();
    // parse `stopIds` and add each value as item to `result`
    return result.build();
  }

  public static void saveRoutes() {
    try (FileOutputStream output = new FileOutputStream(routesProtoAbsPath)) {
      for (Routes route : getRoutes(routesAbsPath)) {
        route.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<Samples> getSamples(Path csvPath){

  }

  public static void saveSamples() {
    try (FileOutputStream output = new FileOutputStream(samplesProtoAbsPath)) {
      for (Sample sample : getSamples(samplesAbsPath)) {
        sample.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  //method to test the routes path
  //private static void printRoutes(String csvPath, int amountLinesToPrint){
    //List<Routes> routes = getRoutes(Paths.get(csvPath));
    //for (int i = 0; i < amountLinesToPrint; i++) {
      //System.out.println(routes.get(i));
    //}
  //}


  static Path routesAbsPath;
  static Path samplesAbsPath;
  static Path stopsAbsPath;
  static Path tripsAbsPath;

  // Paths to output files
  static String routesProtoAbsPath;
  static String samplesProtoAbsPath;
  static String stopsProtoAbsPath;
  static String tripsProtoAbsPath;
  public static void main(String[] args) throws IOException {

    routesAbsPath = Paths.get(args[0]);
    samplesAbsPath = Paths.get(args[1]);
    stopsAbsPath = Paths.get(args[2]);
    tripsAbsPath = Paths.get(args[3]);

    routesProtoAbsPath = args[4];
    samplesProtoAbsPath = args[5];
    stopsProtoAbsPath = args[6];
    tripsProtoAbsPath = args[7];

    saveRoutes();
    //printRoutes("/home/dev/data_csv/routes.csv", 5);

    saveSamples();
    //saveTrips();
  }
}

