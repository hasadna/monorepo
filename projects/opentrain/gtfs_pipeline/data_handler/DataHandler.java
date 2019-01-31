package projects.opentrain.gtfs_pipeline.data_handler;

//imports for proto

import com.projects.opentrain.gtfs_pipeline.AgencyProtos.Agency;
import com.projects.opentrain.gtfs_pipeline.AgencyProtos.Agencies;
import com.projects.opentrain.gtfs_pipeline.CalendarProtos.Calendar;
import com.projects.opentrain.gtfs_pipeline.CalendarProtos.Calendars;
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

  public static Agencies.Builder getAgency(Path csvPath) {
    Agencies.Builder allAgenciesBuilder = Agencies.newBuilder();
    try {
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
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

  public static void main(String[] args) throws IOException {
    //[0]=agency.txt, [1]=calendar.txt, [7]=stops.txt
    Path agency_txt = Paths.get(args[0]);
    Path calendar_txt = Paths.get(args[1]);
    Path stops_txt = Paths.get(args[7]);
    Path outputPath = Paths.get(args[args.length - 1]);
    Files.write(outputPath, TextFormat.printToString(getCalendar(calendar_txt)).getBytes());
  }
}
