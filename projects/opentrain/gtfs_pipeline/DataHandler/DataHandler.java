package projects.opentrain.gtfs_pipeline.DataHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Path;
import java.util.List;

class DataHandler 
{
    public static void main(String[] args) throws IOException{

        System.out.println("Hello!");

        /*Path outfile = Path.get(args[args.length]);
        String str;
        for (String path : args) {
            Path curPath = Path.get(path);
            str.concat(curPath.getFileName() + " lines: ");
            List<String> curPathLines = Files.readAllLines(curPath);
            str.concat(String.valueOf(curPathLines.size()));
        }
        Files.write(outfile, str.getBytes());*/

        /*Path agency = Paths.get(args[0]);
        Path calendar = Paths.get(args[1]);
        Path fare_attributes = Paths.get(args[2]);
        Path fare_rules = Paths.get(args[3]);
        Path routes = Paths.get(args[4]);
        Path shapes = Paths.get(args[5]);
        Path stop_times = Paths.get(args[6]);
        Path stops = Paths.get(args[7]);
        Path translations = Path.get(args[8]);
        Path trips = Path.get(args[9]);*/
    }

}