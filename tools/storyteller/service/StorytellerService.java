package tools.storyteller.service;

import com.google.common.flogger.FluentLogger;
import io.grpc.stub.StreamObserver;
import tools.storyteller.service.Protos.ShareStoriesRequest;
import tools.storyteller.service.Protos.ShareStoriesResponse;
import com.google.startupos.common.firestore.FirestoreProtoClient;
import com.google.startupos.tools.localserver.service.AuthService;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;

/*
 * StorytellerService is a gRPC service for Storyteller.
 */
public class StorytellerService extends StorytellerServiceGrpc.StorytellerServiceImplBase {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();

  @FlagDesc(name = "firestore_storyteller_root", description = "Storyteller root path in Firestore")
  private static final Flag<String> firestoreStorytellerRoot = Flag.create("/storyteller");

  private AuthService authService;

  public StorytellerService(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public void shareStories(
      ShareStoriesRequest req, StreamObserver<ShareStoriesResponse> responseObserver) {
    FirestoreProtoClient client =
        new FirestoreProtoClient(authService.getProjectId(), authService.getToken());
    client.setProtoDocument(firestoreStorytellerRoot.get(), req.getStories());
    responseObserver.onNext(ShareStoriesResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}

