package com.example.tasks;

import com.example.tasks.Protos.User;
import com.example.tasks.Protos.Users;
import com.google.protobuf.TextFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

class Task1 {
  public static void main(String[] args) throws IOException {
    Path file1 = Paths.get(args[0]);
    Path file2 = Paths.get(args[1]);
    Path outfile = Paths.get(args[2]);

    // read list of first and last names from two files
    List<String> firstNames = Files.readAllLines(file1);
    List<String> lastNames = Files.readAllLines(file2);

    Users.Builder allUsersBuilder = Users.newBuilder();

    // combine them into proto messages one-by-one and add to allUsersBuilder
    IntStream.range(0, firstNames.size())
        .mapToObj(
            i ->
                User.newBuilder()
                    .setFirstName(firstNames.get(i))
                    .setLastName(lastNames.get(i))
                    .build())
        .forEach(allUsersBuilder::addUsers);

    // write to output file as prototxt for further processing
    Files.write(outfile, TextFormat.printToString(allUsersBuilder).getBytes());
  }
}

