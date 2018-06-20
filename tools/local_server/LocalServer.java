package tools.local_server;

import com.google.startupos.common.CommonModule;
import dagger.Component;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import com.google.common.flogger.FluentLogger;
import com.google.startupos.tools.localserver.service.AuthService;
import tools.storyteller.service.StorytellerService;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.common.flags.FlagDesc;
import javax.inject.Inject;
import javax.inject.Singleton;

/*
 * LocalServer is a gRPC server that is run locally. It provides services to command-line tools,
 * most notably knowledge of the user and access to the Cloud.
 */

/* To run: bazel build //tools/local_server:local_server_deploy.jar
 * bazel-bin/tools/local_server/local_server -- {absolute_path}
 * {absolute_path} is absolute root path to serve files over (use `pwd` for current dir)
 */
@Singleton
public class LocalServer {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @FlagDesc(name = "local_server_port", description = "Port for local gRPC server")
  public static final Flag<Integer> localServerPort = Flag.create(8001);

  @FlagDesc(name = "root_path", description = "Root path for serving files for reviewer service")
  public static final Flag<String> rootPath = Flag.create("");

  private Server server;

  @Inject
  LocalServer(AuthService authService) {
    server =
        ServerBuilder.forPort(localServerPort.get())
            .addService(authService)
            .addService(new StorytellerService(authService))
            .build();
  }

  private void start() throws IOException {
    server.start();
    logger.atInfo().log("Server started, listening on %s", localServerPort.get());
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                  System.err.println("Shutting down gRPC server since JVM is shutting down");
                  LocalServer.this.stop();
                  System.err.println("Server shut down");
                }));
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  private static void checkFlags() {
    if (rootPath.get().isEmpty()) {
        System.out.println("Error: Please set --root_path");
        System.exit(1);
    }
  }

  @Singleton
  @Component(modules = { CommonModule.class })
  public interface LocalServerComponent {
    LocalServer getLocalServer();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    Flags.parse(
        args,
        LocalServer.class.getPackage(),
        StorytellerService.class.getPackage(),
        AuthService.class.getPackage());
    checkFlags();

    LocalServer server = DaggerLocalServer_LocalServerComponent.builder().build().getLocalServer();
    server.start();
    server.blockUntilShutdown();
  }
}
