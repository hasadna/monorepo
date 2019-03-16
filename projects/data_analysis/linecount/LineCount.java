package projects.data_analysis.linecount;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class LineCount {
    public static void main(String[] args) throws IOException {
            ImmutableList<Path> paths = ImmutableList.of(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2]));
            Path outfile = Paths.get(args[3]);
            StringBuilder result = new StringBuilder();
            for(Path path:paths){
                result.append(path.toString());
                result.append(":");
                result.append( Files.readAllLines(path).size());
                result.append( '\n');
            }
            Files.write(outfile, result.toString().getBytes());
    }
}