package projects.data_analysis.task2;

import com.google.protobuf.TextFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import projects.data_analysis.common.Protos.Users;

class Task2 {
  public static void main(String[] args) throws IOException {
    Path file1 = Paths.get(args[0]);
    Path outfile = Paths.get(args[1]);

    // read prototxt
    String protoTxt = new String(Files.readAllBytes(file1));

    // parse into proto message instance
    Users.Builder builder = Users.newBuilder();
    TextFormat.merge(protoTxt, builder);

    // count all users in parsed proto
    String stats =
        String.format("Total number of users read: %d\n", builder.build().getUsersCount());

    // write out stats for further processing
    Files.write(outfile, stats.getBytes());
  }
}

