package projects.opentrain.gtfs_pipeline.handle_data;
    
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class DataHandler {
      public static void main(String[] args) throws IOException {

        Path outfile = Paths.get(args[2]);
        List<String> first_names = Files.readAllLines(Paths.get(args[0]));
        List<String> last_names = Files.readAllLines(Paths.get(args[1]));

        String str = first_names.get(0);
        Files.write(outfile, str.getBytes());

        /*
        Path outfile = Paths.get(args[args.length]);
        String str = "hello-world\n";
        for (String path : args) {
            Path curPath = Paths.get(path);
            str = str + curPath.getFileName() + " lines: ";
            List<String> curPathLines = Files.readAllLines(curPath);
            str = str + String.valueOf(curPathLines.size());
        }
        Files.write(outfile, str.getBytes());
        */
    }
}