package projects.opentrain.gtfs_pipeline.data_handler;

import com.projects.opentrain.gtfs_pipeline.Protos;
import com.projects.opentrain.gtfs_pipeline.Protos.Agency;
import com.projects.opentrain.gtfs_pipeline.Protos.Route;
import com.projects.opentrain.gtfs_pipeline.Protos.Stop;
import com.projects.opentrain.gtfs_pipeline.Protos.StopTime;
import com.projects.opentrain.gtfs_pipeline.Protos.Translation;
import com.projects.opentrain.gtfs_pipeline.Protos.Trip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

class DataHandler {

  private static String ISRAEL_RAIL_AGENCY_ID = "2";

  public static void saveAgency(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "agency_id",
                  "agency_name",
                  "agency_url",
                  "agency_timezone",
                  "agency_lang",
                  "agency_phone",
                  "agency_fare_url"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        if (record.get("agency_id").equals(ISRAEL_RAIL_AGENCY_ID)) {
          Agency.Builder agency = Agency.newBuilder();
          agency
              .setAgencyId(Integer.parseInt(record.get("agency_id")))
              .setAgencyName(record.get("agency_name"))
              .setAgencyUrl(record.get("agency_url"))
              .setAgencyTimezone(record.get("agency_timezone"))
              .setAgencyLang(record.get("agency_lang"))
              .setAgencyPhone(record.get("agency_phone"))
              .setAgencyFareUrl(record.get("agency_fare_url"))
              .build()
              .writeDelimitedTo(output);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getDayOfToday() {
    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    switch (day) {
      case Calendar.SUNDAY:
        return "sunday";
      case Calendar.MONDAY:
        return "monday";
      case Calendar.TUESDAY:
        return "tuesday";
      case Calendar.WEDNESDAY:
        return "wednesday";
      case Calendar.THURSDAY:
        return "thursday";
      case Calendar.FRIDAY:
        return "friday";
      case java.util.Calendar.SATURDAY:
        return "saturday";
      default:
        return "";
    }
  }

  public static void saveCalendar(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "service_id",
                  "sunday",
                  "monday",
                  "tuesday",
                  "wednesday",
                  "thursday",
                  "friday",
                  "saturday",
                  "start_date",
                  "end_date"));
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
      Date date = new Date();
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        if ((Integer.parseInt(record.get("start_date"))
                >= Integer.parseInt(dateFormat.format(date)))
            && ((Integer.parseInt(record.get("end_date"))
                >= Integer.parseInt(dateFormat.format(date))))
            && Integer.parseInt(record.get(getDayOfToday())) == 1) {
          Protos.Calendar.Builder calendar = Protos.Calendar.newBuilder();
          calendar
              .setServiceId(Integer.parseInt(record.get("service_id")))
              .setSunday(Boolean.parseBoolean(record.get("sunday")))
              .setMonday(Boolean.parseBoolean(record.get("monday")))
              .setTuesday(Boolean.parseBoolean(record.get("tuesday")))
              .setWednesday(Boolean.parseBoolean(record.get("wednesday")))
              .setThursday(Boolean.parseBoolean(record.get("thursday")))
              .setFriday(Boolean.parseBoolean(record.get("friday")))
              .setSaturday(Boolean.parseBoolean(record.get("saturday")))
              .setStartDate(Integer.parseInt(record.get("start_date")))
              .setEndDate(Integer.parseInt(record.get("end_date")))
              .build()
              .writeDelimitedTo(output);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveRoutes(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "route_id",
                  "agency_id",
                  "route_short_name",
                  "route_long_name",
                  "route_desc",
                  "route_type",
                  "route_color"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        Route.Builder route = Route.newBuilder();
        route
            .setRouteId(Integer.parseInt(record.get("route_id")))
            .setAgencyId(Integer.parseInt(record.get("agency_id")))
            .setRouteShortName(record.get("route_short_name"))
            .setRouteLongName(record.get("route_long_name"))
            .setRouteDesc(record.get("route_desc"))
            .setRouteType(Integer.parseInt(record.get("route_type")))
            .setRouteColor(record.get("route_color"))
            .build()
            .writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveStopTimes(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "trip_id",
                  "arrival_time",
                  "departure_time",
                  "stop_id",
                  "stop_sequence",
                  "pickup_type",
                  "drop_off_type",
                  "shape_dist_traveled"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        StopTime.Builder stopTime = StopTime.newBuilder();
        stopTime
            .setTripId(record.get("trip_id"))
            .setArrivalTime(record.get("arrival_time"))
            .setDepartureTime(record.get("departure_time"))
            .setStopId(record.get("stop_id"))
            .setStopSequence(record.get("stop_sequence"))
            .setPickupType(record.get("pickup_type"))
            .setDropOffType(record.get("drop_off_type"))
            .setShapeDistTraveled(record.get("shape_dist_traveled"))
            .build()
            .writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveStops(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "stop_id",
                  "stop_code",
                  "stop_name",
                  "stop_desc",
                  "stop_lat",
                  "stop_lon",
                  "location_type",
                  "parent_station",
                  "zone_id"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        Stop.Builder stop = Stop.newBuilder();
        stop.setStopId(Integer.parseInt(record.get("stop_id")))
            .setStopCode(Integer.parseInt(record.get("stop_code")))
            .setStopName(record.get("stop_name"))
            .setStopDesc(record.get("stop_desc"))
            .setStopLat(Double.parseDouble(record.get("stop_lat")))
            .setStopLon(Double.parseDouble(record.get("stop_lon")))
            .setLocationType(Integer.parseInt(record.get("location_type")))
            .setParentStation(record.get("parent_station"))
            .setZoneId(Integer.parseInt(record.get("zone_id")))
            .build()
            .writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveTranslations(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withQuote(null).withHeader("trans_id", "lang", "translation"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        Translation.Builder translation = Translation.newBuilder();
        translation
            .setTransId(record.get("trans_id"))
            .setLang(record.get("lang"))
            .setTranslation(record.get("translation"))
            .build()
            .writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveTrips(Path csvPath, String path) {
    try (FileOutputStream output = new FileOutputStream(path)) {
      CSVParser parser =
          CSVParser.parse(
              csvPath,
              StandardCharsets.UTF_8,
              CSVFormat.DEFAULT.withHeader(
                  "route_id",
                  "service_id",
                  "trip_id",
                  "trip_headsign",
                  "direction_id",
                  "shape_id"));
      boolean isHeader = true;
      for (CSVRecord record : parser) {
        if (isHeader) {
          isHeader = false;
          continue;
        }
        Trip.Builder trip = Trip.newBuilder();
        trip.setRouteId(Integer.parseInt(record.get("route_id")))
            .setServiceId(Integer.parseInt(record.get("service_id")))
            .setTripId(record.get("trip_id"))
            .setTripHeadsign(record.get("trip_headsign"))
            .setDirectionId(Integer.parseInt(record.get("direction_id")))
            .setShapeId(record.get("shape_id"))
            .build()
            .writeDelimitedTo(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    // Paths to the CSVs files
    final Path agencyTxt = Paths.get(args[0]);
    final Path calendarTxt = Paths.get(args[1]);
    final Path routesTxt = Paths.get(args[2]);
    final Path stopTimesTxt = Paths.get(args[3]);
    final Path stopsTxt = Paths.get(args[4]);
    final Path translationsTxt = Paths.get(args[5]);
    final Path tripsTxt = Paths.get(args[6]);

    // Paths to the output files
    final String agencyOutputPath = args[args.length - 7];
    final String calendarOutputPath = args[args.length - 6];
    final String routesOutputPath = args[args.length - 5];
    final String stopTimesOutputPath = args[args.length - 4];
    final String stopsOutputPath = args[args.length - 3];
    final String translationsOutputPath = args[args.length - 2];
    final String tripsOutputPath = args[args.length - 1];

    saveAgency(agencyTxt, agencyOutputPath);
    saveCalendar(calendarTxt, calendarOutputPath);
    saveRoutes(routesTxt, routesOutputPath);
    saveStopTimes(stopTimesTxt, stopTimesOutputPath);
    saveStops(stopsTxt, stopsOutputPath);
    saveTranslations(translationsTxt, translationsOutputPath);
    saveTrips(tripsTxt, tripsOutputPath);
  }
}

