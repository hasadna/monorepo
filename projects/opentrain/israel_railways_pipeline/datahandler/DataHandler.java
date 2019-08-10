package projects.opentrain.israel_railways_pipeline.datahandler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import projects.opentrain.israel_railways_pipeline.common.RailWayProtos;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class DataHandler {

//
//  public static List<Integer> getRouteIds() {
//    return getRoutes(routesAbsPath).stream().map(Route::getRouteId).collect(Collectors.toList());
//  }
//
//  public static List<Route> getRoutes(Path csvPath) {

//  }
//
//  public static List<Integer> getStopsId() {
//    return getStopTimes(stopTimesAbsPath)
//        .stream()
//        .map(StopTime::getStopId)
//        .collect(Collectors.toList());
//  }
//
//  public static List<StopTime> getStopTimes(Path csvPath) {
//    List<String> tripsId = getTripsId();
//    List<StopTime> stopsTime = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "trip_id",
//                  "arrival_time",
//                  "departure_time",
//                  "stop_id",
//                  "stop_sequence",
//                  "pickup_type",
//                  "drop_off_type",
//                  "shape_dist_traveled"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!tripsId.contains(record.get("trip_id"))) {
//          continue;
//        }
//        StopTime.Builder stopTime = StopTime.newBuilder();
//        stopsTime.add(
//            stopTime
//                .setTripId(record.get("trip_id"))
//                .setArrivalTime(record.get("arrival_time"))
//                .setDepartureTime(record.get("departure_time"))
//                .setStopId(Integer.parseInt(record.get("stop_id")))
//                .setStopSequence(record.get("stop_sequence"))
//                .setPickupType(record.get("pickup_type"))
//                .setDropOffType(record.get("drop_off_type"))
//                .setShapeDistTraveled(record.get("shape_dist_traveled"))
//                .build());
//      }
//      return stopsTime;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<Stop> getStops(Path csvPath) {
//    List<Integer> stopsId = getStopsId();
//    List<Stop> stops = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "stop_id",
//                  "stop_code",
//                  "stop_name",
//                  "stop_desc",
//                  "stop_lat",
//                  "stop_lon",
//                  "location_type",
//                  "parent_station",
//                  "zone_id"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!stopsId.contains(Integer.parseInt(record.get("stop_id")))) {
//          continue;
//        }
//        Stop.Builder stop = Stop.newBuilder();
//        stops.add(
//            stop.setStopId(Integer.parseInt(record.get("stop_id")))
//                .setStopCode(Integer.parseInt(record.get("stop_code")))
//                .setStopName(record.get("stop_name"))
//                .setStopDesc(record.get("stop_desc"))
//                .setStopLat(Double.parseDouble(record.get("stop_lat")))
//                .setStopLon(Double.parseDouble(record.get("stop_lon")))
//                .setLocationType(Integer.parseInt(record.get("location_type")))
//                .setParentStation(record.get("parent_station"))
//                .setZoneId(Integer.parseInt(record.get("zone_id")))
//                .build());
//      }
//      return stops;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<Translation> getTranslations(Path csvPath) {
//    List<Translation> translations = new ArrayList<>();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withQuote(null).withHeader("trans_id", "lang", "translation"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        Translation.Builder translation = Translation.newBuilder();
//        translations.add(
//            translation
//                .setTransId(record.get("trans_id"))
//                .setLang(record.get("lang"))
//                .setTranslation(record.get("translation"))
//                .build());
//      }
//      return translations;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  public static List<String> getTripsId() {
//    return getTrips(tripsAbsPath).stream().map(Trip::getTripId).collect(Collectors.toList());
//  }
//
//  public static List<Trip> getTrips(Path csvPath) {
//    List<Trip> trips = new ArrayList<>();
//    List<Integer> routesId = getRouteIds();
//    List<Integer> serviceIds = getServiceIds();
//    try {
//      CSVParser parser =
//          CSVParser.parse(
//              csvPath,
//              StandardCharsets.UTF_8,
//              CSVFormat.DEFAULT.withHeader(
//                  "route_id",
//                  "service_id",
//                  "trip_id",
//                  "trip_headsign",
//                  "direction_id",
//                  "shape_id"));
//      boolean isHeader = true;
//      for (CSVRecord record : parser) {
//        if (isHeader) {
//          isHeader = false;
//          continue;
//        }
//        if (!routesId.contains(Integer.parseInt(record.get("route_id")))
//            || !serviceIds.contains(Integer.parseInt(record.get("service_id")))) {
//          continue;
//        }
//        Trip.Builder trip = Trip.newBuilder();
//        trips.add(
//            trip.setRouteId(Integer.parseInt(record.get("route_id")))
//                .setServiceId(Integer.parseInt(record.get("service_id")))
//                .setTripId(record.get("trip_id"))
//                .setTripHeadsign(record.get("trip_headsign"))
//                .setDirectionId(Integer.parseInt(record.get("direction_id")))
//                .setShapeId(record.get("shape_id"))
//                .build());
//      }
//      return trips;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//



    public static List<RailWayProtos.Routes> getRoutes(Path sampleCsv) {

        List<RailWayProtos.Routes> routesList = new ArrayList<>();
        try {
            CSVParser parser =
                    CSVParser.parse(
                            sampleCsv,
                            StandardCharsets.UTF_8,
                            CSVFormat.DEFAULT.withHeader("id", "stop_ids"
                            ));
            boolean isHeader = true;
            for (CSVRecord record : parser) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }


                RailWayProtos.Routes.Builder routeBuilder = RailWayProtos.Routes.newBuilder();
                routesList.add(
                        routeBuilder
                                .setId(Integer.parseInt(record.get("id")))
                                .setStopIds(record.get("stop_ids"))
                                .build());
            }
            return routesList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void saveRoutes(Path path, String outputName) {
        try (FileOutputStream output = new FileOutputStream(outputName)) {
            for (RailWayProtos.Routes route : getRoutes(path)) {
//                route.writeDelimitedTo(output);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



//  public static void saveStops() {
//    try (FileOutputStream output = new FileOutputStream(stopsProtoAbsPath)) {
//      for (Stop stop : getStops(stopsAbsPath)) {
//        stop.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//

//  public static void saveTrips() {
//    try (FileOutputStream output = new FileOutputStream(tripsProtoAbsPath)) {
//      for (Trip trip : getTrips(tripsAbsPath)) {
//        trip.writeDelimitedTo(output);
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  // Paths to the CSVs files
//  static Path routesAbsPath;
//  // Paths to output files
//  static String routesProtoAbsPath;

    public static void main(String[] args) {

//    {
//      routesAbsPath = Paths.get(args[0]);
//      routesProtoAbsPath = args[1];

//    }
//        getRoutes(Paths.get(args[0]));
//        System.out.println(getRoutes(Paths.get(args[0])));
        saveRoutes(Paths.get(args[0]), args[1]);

    }
}

