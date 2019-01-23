package projects.opentrain.gtfs_pipeline.data_handler;
    
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.lang.String;

class DataHandler {
      public static void main(String[] args) throws IOException {
        String output = "";
        Path outputPath = Paths.get(args[args.length-1]);
        for (String path : args) {
            // TODO: Read the files in a streaming manner since they are large
            Path curPath = Paths.get(path);
            output += curPath.getFileName() + " lines: ";
            Files.write(outputPath, new String(curPath.getFileName()+"\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }
        System.out.print(output);
    }
}
