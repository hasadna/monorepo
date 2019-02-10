package projects.opentrain.gtfs_pipeline.data_handler;

import com.projects.opentrain.gtfs_pipeline.AgencyProtos.Agency;
import com.projects.opentrain.gtfs_pipeline.CalendarProtos.Calendar;
import com.projects.opentrain.gtfs_pipeline.FareAttributesProtos.FareAttributes;
import com.projects.opentrain.gtfs_pipeline.FareRulesProtos.FareRules;
import com.projects.opentrain.gtfs_pipeline.RoutesProtos.Route;
import com.projects.opentrain.gtfs_pipeline.ShapesProtos.Shape;
import com.projects.opentrain.gtfs_pipeline.StopTimeListProtos.StopTime;
import com.projects.opentrain.gtfs_pipeline.StopsProtos.Stop;
import com.projects.opentrain.gtfs_pipeline.TranslationsProtos.Translation;
import com.projects.opentrain.gtfs_pipeline.TripsProtos.Trip;
import com.google.protobuf.TextFormat;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.omg.CORBA.portable.InputStream;
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

import javax.management.RuntimeErrorException;

import java.lang.String;
import java.lang.Iterable;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Iterator;
import java.lang.Object;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;

class DataHandler {

  public static void saveAgency(Path csvPath, String path) {
     
      // where we serialize the messages
     try (FileOutputStream output = new FileOutputStream(path);)

      {
        CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
        .withHeader(
          "agency_id",
          "agency_name",
          "agency_url",
          "agency_timezone",
          "agency_lang",
          "agency_phone",
          "agency_fare_url"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        Agency.Builder agency = Agency.newBuilder();
        agency.setAgencyId(Integer.parseInt(record.get("agency_id")))
              .setAgencyName(record.get("agency_name"))
              .setAgencyUrl(record.get("agency_url"))
              .setAgencyTimezone(record.get("agency_timezone"))
              .setAgencyLang(record.get("agency_lang"))
              .setAgencyPhone(record.get("agency_phone"))
              .setAgencyFareUrl(record.get("agency_fare_url"))
              .build()
              .writeDelimitedTo(output);
      }
      output.close();
  } catch (IOException e) {
    throw new RuntimeException(e);
    }
  }

  public static void saveCalendar(Path csvPath, String path) {
     
      // where we serialize the messages
    try(FileOutputStream output = new FileOutputStream(path);)

    {  
      CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
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
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        Calendar.Builder calendar = Calendar.newBuilder();
        calendar.setServiceId(Integer.parseInt(record.get("service_id")))
                .setSunday(Integer.parseInt(record.get("sunday")))
                .setMonday(Integer.parseInt(record.get("monday")))
                .setTuesday(Integer.parseInt(record.get("tuesday")))
                .setWednesday(Integer.parseInt(record.get("wednesday")))
                .setThursday(Integer.parseInt(record.get("thursday")))
                .setFriday(Integer.parseInt(record.get("friday")))
                .setSaturday(Integer.parseInt(record.get("saturday")))
                .setStartDate(Integer.parseInt(record.get("start_date")))
                .setEndDate(Integer.parseInt(record.get("end_date")))
                .build()
                .writeDelimitedTo(output);
      }
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveFareAttributes(Path csvPath, String path) {
       
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "fare_id",
        "price",
        "currency_type",
        "payment_method",
        "transfers",
        "agency_id",
        "transfer_duration"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        FareAttributes.Builder fareAttributes = FareAttributes.newBuilder();
            fareAttributes.setFareId(Integer.parseInt(record.get("fare_id")))
                .setPrice(Double.parseDouble(record.get("price")))
                .setCurrencyType(record.get("currency_type"))
                .setPaymentMethod(Integer.parseInt(record.get("payment_method")))
                .setTransfers(Integer.parseInt(record.get("transfers")))
                .setAgencyId(Integer.parseInt(record.get("agency_id")))
                .setTransferDuration(record.get("transfer_duration"))
                .build()
                .writeDelimitedTo(output);
      }
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveFareRules(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "fare_id",
        "route_id",
        "origin_id",
        "destination_id",
        "contains_id"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        FareRules.Builder fareRule = FareRules.newBuilder();
        fareRule.setFareId(Integer.parseInt(record.get("fare_id")))
            .setRouteId(Integer.parseInt(record.get("route_id")))
            .setOriginId(Integer.parseInt(record.get("origin_id")))
            .setDestinationId(Integer.parseInt(record.get("destination_id")))
            .setContainsId(record.get("contains_id"))
            .build()
            .writeDelimitedTo(output);
          }
          output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveRoutes(Path csvPath, String path) {
     
       // where we serialize the messages
       try(FileOutputStream output = new FileOutputStream(path);)
 
      { CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
       .withHeader(
         "route_id",
         "agency_id",
         "route_short_name",
         "route_long_name",
         "route_desc",
         "route_type",
         "route_color"));
       // Creat iterator - to create from each record a message
       Iterator<CSVRecord> iterator = parser.iterator();
       // We need to ignore the first CSVRecord which holds the csv headers
       iterator.next();
       while (iterator.hasNext()) {
         CSVRecord record = iterator.next();
         Route.Builder route = Route.newBuilder();
        route.setRouteId(Integer.parseInt(record.get("route_id")))
        .setAgencyId(Integer.parseInt(record.get("agency_id")))
                .setRouteShortName(record.get("route_short_name"))
                .setRouteLongName(record.get("route_long_name"))
                .setRouteDesc(record.get("route_desc"))
                .setRouteType(Integer.parseInt(record.get("route_type")))
                .setRouteColor(record.get("route_color"))
                .build()
                .writeDelimitedTo(output);
              }
              output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveShapes(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "shape_id",
        "shape_pt_lat",
        "shape_pt_lon",
        "shape_pt_sequence"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        Shape.Builder shap = Shape.newBuilder();
        shap.setShapeId(Integer.parseInt(record.get("shape_id")))
            .setShapePtLat(Double.parseDouble(record.get("shape_pt_lat")))
            .setShapePtLot(Double.parseDouble(record.get("shape_pt_lon")))
            .setShapePtSequence(Integer.parseInt(record.get("shape_pt_sequence")))
            .build()
            .writeDelimitedTo(output);
      }
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveStopTimes(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
         "trip_id",
         "arrival_time",
         "departure_time",
         "stop_id",
         "stop_sequence",
         "pickup_type",
         "drop_off_type",
         "shape_dist_traveled"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        StopTime.Builder stopTime = StopTime.newBuilder();
        stopTime.setTripId(record.get("trip_id"))
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
      output.close();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveStops(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "stop_id",
        "stop_code",
        "stop_name",
        "stop_desc",
        "stop_lat",
        "stop_lon",
        "location_type",
        "parent_station",
        "zone_id"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
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
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveTranslations(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "trans_id",
        "lang",
        "translation"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
        Translation.Builder translation = Translation.newBuilder();
            translation.setTransId(record.get("trans_id"))
            .setLang(record.get("lang"))
            .setTranslation(record.get("translation"))
            .build()
            .writeDelimitedTo(output);
      }
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveTrips(Path csvPath, String path) {
     
      // where we serialize the messages
      try(FileOutputStream output = new FileOutputStream(path);)

    {  CSVParser parser = CSVParser.parse(csvPath, StandardCharsets.UTF_8, CSVFormat.DEFAULT
      .withHeader(
        "route_id",
        "service_id",
        "trip_id",
        "trip_headsign",
        "direction_id",
        "shape_id"));
      // Creat iterator - to create from each record a message
      Iterator<CSVRecord> iterator = parser.iterator();
      // We need to ignore the first CSVRecord which holds the csv headers
      iterator.next();
      while (iterator.hasNext()) {
        CSVRecord record = iterator.next();
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
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  //TEST: test by deserialize and reach last message and print it
  //we deserialize the file and create Message Object for each message (that coded into the file by the "writeDelimitedTo" method)
  public static void test(String[] paths){

    //test for Agency
    System.out.println("-----------------------Agency test result-----------------------");
    try(FileInputStream in = new FileInputStream(paths[0]);){
      for (;;) {
        Agency a = Agency.parseDelimitedFrom(in);
        //if we already pared everything then break the loop
        if(a == null)
          break;
        if (a.getAgencyId() == 51)
                  System.out.println(a.toString());
      }
    }
    catch(IOException e){throw new RuntimeException(e);}
    //test for Calendar
    System.out.println("-----------------------Calendar test result-----------------------");
    try(FileInputStream in = new FileInputStream(paths[1]);){
     for (;;) {
        Calendar c = Calendar.parseDelimitedFrom(in);
        if(c == null)
        break;
        if (c.getServiceId() == 19079)
          System.out.println(c.toString());
      }
    }

    catch(IOException e){throw new RuntimeException(e);}
      //test for FareAttributes
      System.out.println("-----------------------fareAttributes test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[2]);){
      for (;;) {
        FareAttributes f = FareAttributes.parseDelimitedFrom(in);
        if(f==null)
          break;
        if (f.getFareId() == 702) // the last fareId is 702 here we test if it reach the last message
          System.out.println(f.toString());
      }}
      catch(IOException e){throw new RuntimeException(e);}
      //test for FareRules
      System.out.println("-----------------------FareRules test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[3]);){
      for (;;) {
            FareRules f = FareRules.parseDelimitedFrom(in);
            if(f == null)
              break;
            if (f.getFareId() == 441 && f.getRouteId() == 1136) 
              System.out.println(f.toString());
          }}
          catch(IOException e){throw new RuntimeException(e);}
      //test for Routes
      System.out.println("-----------------------Routes test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[4]);){
      for (;;) {
           Route r = Route.parseDelimitedFrom(in);
           if(r == null)
              break;
           if (r.getRouteId() == 25341)
           System.out.println(r.toString());
              }}
              catch(IOException e){throw new RuntimeException(e);}
      //test for Shapes
      System.out.println("-----------------------Shapes test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[5]);){

      for (;;) {
        Shape s = Shape.parseDelimitedFrom(in);
        if(s == null)
          break;
        if (s.getShapeId() == 102763) // the last agency_id is 51 here we test if it reach the last message
          System.out.println(s.toString());
      }}
      catch(IOException e){throw new RuntimeException(e);}
      //test for StopTimes
      System.out.println("-----------------------StopTimes test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[6]);){

      for (;;) {
        StopTime st = StopTime.parseDelimitedFrom(in);
        if(st == null)
          break;
        if (st.getTripId().equals("9967472_160119") && st.getArrivalTime().equals("11:44:40")
            && st.getDepartureTime().equals("11:44:40") && st.getStopId().equals("34503")
            && st.getStopSequence().equals("19") && st.getPickupType().equals("1") && st.getDropOffType().equals("0")
            && st.getShapeDistTraveled().equals("39581"))
          System.out.println(st.toString());
      }}
      catch(IOException e){throw new RuntimeException(e);}
      //test for Stops
      System.out.println("-----------------------Stops test result-----------------------");

      try(FileInputStream in = new FileInputStream(paths[7]);){

      for (;;) {
        Stop s = Stop.parseDelimitedFrom(in);
        if(s==null)
          break;
        if (s.getStopId() == 42972) 
          System.out.println(s.toString());
      }}
      catch(IOException e){throw new RuntimeException(e);}
      /*
      //test for Translations
      System.out.println("-----------------------Translations test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[8]);){

      for (;;) {
        t = Translation.parseDelimitedFrom(in);
        if(t==null)
          break;
          System.out.println(t.toString()); //print every message
      }} 
      catch(IOException e){throw new RuntimeException(e);}*/

      //test for Trips
      System.out.println("-----------------------Trips test result-----------------------");
      try(FileInputStream in = new FileInputStream(paths[9]);){
      for (;;) {
        Trip t = Trip.parseDelimitedFrom(in);
        if(t==null)
        break;
        if (t.getTripId().equals("36453859_250119"))
          System.out.println(t.toString());
      }}
     catch(IOException e){throw new RuntimeException(e);}
  }
  

  public static void main(String[] args) throws IOException {
    // Paths to the CSVs files
    Path agency_txt = Paths.get(args[0]);
    Path calendar_txt = Paths.get(args[1]);
    Path fare_attributes_txt = Paths.get(args[2]);
    Path fare_rules_txt = Paths.get(args[3]);
    Path routes_txt = Paths.get(args[4]);
    Path shapes_txt = Paths.get(args[5]); 
    Path stop_times_txt = Paths.get(args[6]); 
    Path stops_txt = Paths.get(args[7]);
    Path translations_txt = Paths.get(args[8]);  //NOTICE: problem with CSVParser
    Path trips_txt = Paths.get(args[9]);

    //Paths to the output files
    String agency_p = args[args.length - 10];
    String calendar_p = args[args.length - 9];
    String fare_attributes_p = args[args.length - 8];
    String fare_rules_p = args[args.length - 7];
    String routes_p = args[args.length - 6];
    String shapes_p = args[args.length - 5]; 
    String stop_times_p = args[args.length - 4]; 
    String stops_p = args[args.length - 3];
    String translations_p = args[args.length - 2];  //NOTICE: problem with CSVParser
    String trips_p = args[args.length - 1];

    String[] outPaths = {agency_p, calendar_p, fare_attributes_p, fare_rules_p, routes_p, shapes_p, stop_times_p, stops_p, translations_p, trips_p};

     saveAgency(agency_txt, agency_p);
     saveCalendar(calendar_txt, calendar_p);
     saveFareAttributes(fare_attributes_txt, fare_attributes_p);
     saveFareRules(fare_rules_txt, fare_rules_p);
     saveRoutes(routes_txt, routes_p);
     saveShapes(shapes_txt, shapes_p);
     saveStopTimes(stop_times_txt, stop_times_p);
     saveStops(stops_txt, stops_p);
     //saveTranslations(translations_txt, translations_p); //NOTICE: problem with CSVParser
     saveTrips(trips_txt, trips_p);
     
    test(outPaths);

    //TODO: Remove this line after fix the saveTranslations
    Files.write(Paths.get(translations_p), "".getBytes());
  }
}
