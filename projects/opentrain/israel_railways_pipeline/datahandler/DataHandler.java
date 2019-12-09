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

  public static void saveSamples(Path path, String outputName) {
    try (FileOutputStream output = new FileOutputStream(outputName)) {
      for (RailWayProtos.Samples sample : getSamples(path)) {
        sample.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<RailWayProtos.Samples> getSamples(Path stopCsv) {
    List<RailWayProtos.Samples> sampleList = new ArrayList<>();
    try {
      CSVParser parser =
          CSVParser.parse(
              stopCsv,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "id",
                  "gtfs_stop_id",
                  "is_source",
                  "is_dest",
                  "actual_arrival",
                  "exp_arrival",
                  "actual_departure",
                  "exp_departure",
                  "delay_arrival",
                  "delay_departure",
                  "index",
                  "filename",
                  "line_number",
                  "valid",
                  "invalid_reason",
                  "stop_id",
                  "trip_id",
                  "sheet_idx",
                  "actual_departure_fixed",
                  "actual_arrival_fixed",
                  "ignored_error"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }

        RailWayProtos.Samples.Builder sampleBuilder = RailWayProtos.Samples.newBuilder();
        sampleList.add(
            sampleBuilder
                .setId(Integer.parseInt(record.get("id")))
                .setGtfsStopId(Integer.parseInt(record.get("gtfs_stop_id")))
                .setIsSource(Boolean.parseBoolean(record.get("is_source")))
                .setIsDest(Boolean.parseBoolean(record.get("is_dest")))
                .setActualArrival(record.get("actual_arrival"))
                .setExpArrival(record.get("exp_arrival"))
                .setActualDeparture(record.get("actual_departure"))
                .setExpDeparture(record.get("exp_departure"))
                .setDelayArrival(record.get("delay_arrival"))
                .setDelayDeparture(record.get("delay_departure"))
                .setIndex(Integer.parseInt(record.get("index")))
                .setFilename(record.get("filename"))
                .setLineNumber(Integer.parseInt(record.get("line_number")))
                .setValid(Boolean.parseBoolean(record.get("valid")))
                .setInvalidReason(record.get("invalid_reason"))
                .setStopId(Integer.parseInt(record.get("stop_id")))
                .setTripId(Integer.parseInt(record.get("trip_id")))
                .setSheetIdx(record.get("sheet_idx"))
                .setActualDepartureFixed(record.get("actual_departure_fixed"))
                .setActualArrivalFixed(record.get("actual_arrival_fixed"))
                .setIgnoredError(record.get("ignored_error"))
                .build());
      }
      return sampleList;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveStops(Path path, String outputName) {
    try (FileOutputStream output = new FileOutputStream(outputName)) {
      for (RailWayProtos.Stops stop : getStops(path)) {
        stop.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<RailWayProtos.Stops> getStops(Path stopCsv) {
    List<RailWayProtos.Stops> stopsList = new ArrayList<>();
    try {
      CSVParser parser =
          CSVParser.parse(
              stopCsv,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "id", "gtfs_stop_id", "english", "hebrews", "lat", "lon"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }

        RailWayProtos.Stops.Builder stopBuilder = RailWayProtos.Stops.newBuilder();
        stopsList.add(
            stopBuilder
                .setId(Integer.parseInt(record.get("id")))
                .setGtfsStopId(record.get("gtfs_stop_id"))
                .setEnglish(record.get("english"))
                .setHebrews(record.get("hebrews"))
                .setLat(record.get("lat"))
                .setLon(record.get("lon"))
                .build());
      }
      return stopsList;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveTrips(Path path, String outputName) {
    try (FileOutputStream output = new FileOutputStream(outputName)) {
      for (RailWayProtos.Trips trip : getTrips(path)) {
        trip.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<RailWayProtos.Trips> getTrips(Path tripsCsv) {
    List<RailWayProtos.Trips> tripsList = new ArrayList<>();
    try {
      CSVParser parser =
          CSVParser.parse(
              tripsCsv,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "id",
                  "train_num",
                  "date",
                  "valid",
                  "invalid_reason",
                  "x_week_day_local",
                  "x_hour_local",
                  "route_id",
                  "x_avg_delay_arrival",
                  "x_cache_version",
                  "x_max2_delay_arrival",
                  "x_before_last_delay_arrival",
                  "x_last_delay_arrival"));

      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }

        RailWayProtos.Trips.Builder tripBuilder = RailWayProtos.Trips.newBuilder();
        tripsList.add(
            tripBuilder
                .setId(record.get("id"))
                .setTrainNum(record.get("train_num"))
                .setDate(record.get("date"))
                .setValid(record.get("valid"))
                .setInvalidReason(record.get("invalid_reason"))
                .setXWeekDayLocal(record.get("x_week_day_local"))
                .setXHourLocal(record.get("x_hour_local"))
                .setRouteId(record.get("route_id"))
                .setXAvgDelayArrival(record.get("x_avg_delay_arrival"))
                .setXCacheVersion(record.get("x_cache_version"))
                .setXMax2DelayArrival(record.get("x_max2_delay_arrival"))
                .setXBeforeLastDelayArrival(record.get("x_before_last_delay_arrival"))
                .setXLastDelayArrival(record.get("x_last_delay_arrival"))
                .build());
      }
      return tripsList;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<RailWayProtos.Routes> getRoutes(Path routeCsv) {

    List<RailWayProtos.Routes> routesList = new ArrayList<>();
    try {
      CSVParser parser =
          CSVParser.parse(
              routeCsv, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withHeader("id", "stop_ids"));
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
        route.writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

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
    saveSamples(Paths.get(args[2]), (args[3]));
    saveStops(Paths.get(args[4]), args[5]);
    saveTrips(Paths.get(args[6]), args[7]);
  }
}

