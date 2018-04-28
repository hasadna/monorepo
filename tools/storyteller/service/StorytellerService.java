package tools.storyteller.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import tools.storyteller.service.Protos.ShareStoriesRequest;
import tools.storyteller.service.Protos.ShareStoriesResponse;
import com.google.startupos.common.firestore.FirestoreClient;
import com.google.startupos.tools.localserver.service.AuthService;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;

/*
 * StorytellerService is a gRPC service for Storyteller.
 */
public class StorytellerService extends StorytellerServiceGrpc.StorytellerServiceImplBase {
  private static final Logger logger = Logger.getLogger(StorytellerService.class.getName());
 
  @FlagDesc(name = "firestore_storyteller_root", description = "Storyteller root path in Firestore")
  private static final Flag<String> firestoreStorytellerRoot = Flag.create("/storyteller");

  private AuthService authService;

  public StorytellerService(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public void shareStories(ShareStoriesRequest req, StreamObserver<ShareStoriesResponse> responseObserver) {
    FirestoreClient client = new FirestoreClient(authService.getProjectId(), authService.getToken());
    client.createDocument(firestoreStorytellerRoot.get(), req.getStories());
    responseObserver.onNext(ShareStoriesResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
