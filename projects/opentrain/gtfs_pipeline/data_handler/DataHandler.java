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
        System.out.println(args[0]);
        String outPut = "";
        Path outfilePath = Paths.get(args[args.length-1]);
        for (String path : args) {
            Path curPath = Paths.get(path);
            List<String> curPathContent = Files.readAllLines(curPath);
            outPut = outPut + curPath.getFileName() + " lines: ";
            outPut = outPut + String.valueOf(curPathContent.size()) + "\n";
            Files.write(outfilePath, new String(curPath.getFileName()+"\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            Files.write(outfilePath, curPathContent, StandardOpenOption.APPEND);
            Files.write(outfilePath, new String("\n").getBytes(), StandardOpenOption.APPEND);

        }
        System.out.print(outPut);
    }
}