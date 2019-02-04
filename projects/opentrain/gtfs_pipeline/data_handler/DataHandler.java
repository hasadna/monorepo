package projects.opentrain.gtfs_pipeline.data_handler;

//imports for proto

import com.projects.opentrain.gtfs_pipeline.AgencyProtos.Agency;
import com.projects.opentrain.gtfs_pipeline.AgencyProtos.Agencies;
import com.projects.opentrain.gtfs_pipeline.CalendarProtos.Calendar;
import com.projects.opentrain.gtfs_pipeline.CalendarProtos.Calendars;
import com.projects.opentrain.gtfs_pipeline.FareAttributesProtos.FareAttributes;
import com.projects.opentrain.gtfs_pipeline.FareAttributesProtos.FaresAttributes;
import com.projects.opentrain.gtfs_pipeline.FareRulesProtos.FareRules;
import com.projects.opentrain.gtfs_pipeline.FareRulesProtos.FaresRules;
import com.projects.opentrain.gtfs_pipeline.RoutesProtos.Route;
import com.projects.opentrain.gtfs_pipeline.RoutesProtos.Routes;
import com.projects.opentrain.gtfs_pipeline.ShapesProtos.Shape;
import com.projects.opentrain.gtfs_pipeline.ShapesProtos.Shapes;
import com.projects.opentrain.gtfs_pipeline.StopTimesProtos.StopTimes;
import com.projects.opentrain.gtfs_pipeline.StopTimesProtos.StopsTimes;
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stop;
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stops;
import com.projects.opentrain.gtfs_pipeline.TranslationsProtos.Translation;
import com.projects.opentrain.gtfs_pipeline.TranslationsProtos.Translations;
import com.projects.opentrain.gtfs_pipeline.TripsProtos.Trip;
import com.projects.opentrain.gtfs_pipeline.TripsProtos.Trips;
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

  public static Agencies.Builder getAgency(Path csvPath) {
    Agencies.Builder allAgenciesBuilder = Agencies.newBuilder();
    try {
      //Creates a parser for the given path (csvPath argument)
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      //A list of CSVrecordes which each one represent a row in the csv file
      List<CSVRecord> csvRecords = parser.getRecords();
      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allAgenciesBuilder.addAgencies(
            Agency.newBuilder().setAgencyId(Integer.parseInt(record.get(0))).setAgencyName(record.get(1))
                .setAgencyUrl(record.get(2)).setAgencyTimezone(record.get(3)).setAgencyLang(record.get(4))
                .setAgencyPhone(record.get(5)).setAgencyFareUrl(record.get(6)).build());
      }
    } catch (IOException e) {
    }
    return allAgenciesBuilder;
  }

  public static Calendars.Builder getCalendar(Path csvPath) {
    Calendars.Builder allCalendarsBuilder = Calendars.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();
      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allCalendarsBuilder.addCalendars(
            Calendar.newBuilder().setServiceId(Integer.parseInt(record.get(0)))
			.setSunday(Integer.parseInt(record.get(1))).setMonday(Integer.parseInt(record.get(2)))
			.setTuesday(Integer.parseInt(record.get(3))).setWednesday(Integer.parseInt(record.get(4)))
			.setThursday(Integer.parseInt(record.get(5))).setFriday(Integer.parseInt(record.get(6)))
			.setSaturday(Integer.parseInt(record.get(7))).setStartDate(Integer.parseInt(record.get(8)))
			.setEndDate(Integer.parseInt(record.get(9))).build());
      }
    } catch (IOException e) {
    }
    return allCalendarsBuilder;
  }

  public static FaresAttributes.Builder getFareAttributes(Path csvPath) {
    FaresAttributes.Builder allFaresAttributesBuilder = FaresAttributes.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();
      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allFaresAttributesBuilder.addFaresattributes(
            FareAttributes.newBuilder().setFareId(Integer.parseInt(record.get(0)))
			.setPrice(Double.parseDouble(record.get(1))).setCurrencyType(record.get(2))
			.setPaymentMethod(Integer.parseInt(record.get(3))).setTransfers(Integer.parseInt(record.get(4)))
			.setAgencyId(Integer.parseInt(record.get(5))).setTransferDuration(record.get(6)).build());
      }
    } catch (IOException e) {
    }
    return allFaresAttributesBuilder;
  }

  public static FaresRules.Builder getFareRules(Path csvPath) {
    FaresRules.Builder allFaresRulesBuilder = FaresRules.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();
      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allFaresRulesBuilder.addFaresrules(
            FareRules.newBuilder().setFareId(Integer.parseInt(record.get(0)))
			.setRouteId(Integer.parseInt(record.get(1))).setOriginId(Integer.parseInt(record.get(2)))
			.setDestinationId(Integer.parseInt(record.get(3))).setContainsId(record.get(4)).build());
      }
    } catch (IOException e) {
    }
    return allFaresRulesBuilder;
  }

  public static Routes.Builder getRoutes(Path csvPath) {
    Routes.Builder allRoutesBuilder = Routes.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allRoutesBuilder.addRoutes(
            Route.newBuilder().setRouteId(Integer.parseInt(record.get(0))).setAgencyId(Integer.parseInt(record.get(1)))
                .setRouteShortName(record.get(2)).setRouteLongName(record.get(3)).setRouteDesc(record.get(4))
                .setRouteType(Integer.parseInt(record.get(5))).setRouteColor(record.get(6)).build());
      }
    } catch (IOException e) {
    }
    return allRoutesBuilder;
  }

  public static Shapes.Builder getShapes(Path csvPath) {
    Shapes.Builder allShapesBuilder = Shapes.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allShapesBuilder.addShapes(
            Shape.newBuilder().setShapeId(Integer.parseInt(record.get(0))).setShapePtLat(Double.parseDouble(record.get(1)))
            .setShapePtLot(Double.parseDouble(record.get(2))).setShapePtSequence(Integer.parseInt(record.get(3))).build());
      }
    } catch (IOException e) {
    }
    return allShapesBuilder;
  }

  public static StopsTimes.Builder getStopTimes(Path csvPath) {
    StopsTimes.Builder allstopstimesBuilder = StopsTimes.newBuilder();
    try {
      //Creates a parser for the given path (csvPath argument)      
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      //A list of CSVrecordes which each one represent a row in the csv file
      List<CSVRecord> csvRecords = parser.getRecords();

      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allstopstimesBuilder.addStopstimes(
            StopTimes.newBuilder().setTripId(record.get(0)).setArrivalTime(record.get(1))
                .setDepartureTime(record.get(2)).setStopId(Integer.parseInt(record.get(3))).setStopSequence(Integer.parseInt(record.get(4)))
                .setPickupType(Integer.parseInt(record.get(5))).setDropOffType(Integer.parseInt(record.get(6)))
                .setShapeDistTraveled(Integer.parseInt(record.get(7))).build());
      }
    } catch (IOException e) {
    }
    return allstopstimesBuilder;
  }

  public static Stops.Builder getStops(Path csvPath) {
    Stops.Builder allStopsBuilder = Stops.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allStopsBuilder.addStops(
            Stop.newBuilder().setStopId(Integer.parseInt(record.get(0))).setStopCode(Integer.parseInt(record.get(1)))
                .setStopName(record.get(2)).setStopDesc(record.get(3)).setStopLat(Double.parseDouble(record.get(4)))
                .setStopLon(Double.parseDouble(record.get(5))).setLocationType(Integer.parseInt(record.get(6)))
                .setParentStation(record.get(7)).setZoneId(Integer.parseInt(record.get(8))).build());
      }
    } catch (IOException e) {
    }
    return allStopsBuilder;
  }
  
  public static Translations.Builder getTranslations(Path csvPath) {
    Translations.Builder allTranslationsBuilder = Translations.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();
      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        System.out.println(record);
        allTranslationsBuilder.addTranslations(
            Translation.newBuilder().setTransId(record.get(0)).setLang(record.get(1))
                .setTranslation(record.get(2)).build());
      }
    } catch (IOException e) {
    }
    return allTranslationsBuilder;
  }

  public static Trips.Builder getTrips(Path csvPath) {
    Trips.Builder allTripsBuilder = Trips.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
      List<CSVRecord> csvRecords = parser.getRecords();

      // remove the first record which are columns title
      csvRecords.remove(csvRecords.get(0));
      // iterate each record, create a message with builder
      for (CSVRecord record : csvRecords) {
        allTripsBuilder.addTrips(
            Trip.newBuilder().setRouteId(Integer.parseInt(record.get(0))).setServiceId(Integer.parseInt(record.get(1)))
                .setTripId(record.get(2)).setTripHeadsign(record.get(3)).setDirectionId(Integer.parseInt(record.get(4)))
                .setShapeId(record.get(5)).build());
      }
    } catch (IOException e) {
    }
    return allTripsBuilder;
  }

  public static void main(String[] args) throws IOException {
    //Path
    Path agency_txt = Paths.get(args[0]);
    Path calendar_txt = Paths.get(args[1]);
    Path fare_attributes_txt = Paths.get(args[2]);
    Path fare_rules_txt = Paths.get(args[3]);
    Path routes_txt = Paths.get(args[4]);
    Path shapes_txt = Paths.get(args[5]);       //didn't work
    Path stop_times_txt = Paths.get(args[6]);  //didn't work
    Path stops_txt = Paths.get(args[7]);
    Path translations_txt = Paths.get(args[8]); //didn't work
    Path trips_txt = Paths.get(args[9]);
    //last argument is the output path
    Path outputPath = Paths.get(args[args.length - 1]);
    //Save the proto which returned from the "get*Somthing*" function to the output path as belew
    Files.write(outputPath, TextFormat.printToUnicodeString(getStopTimes(stop_times_txt)).getBytes(UTF_8));
  }
}
