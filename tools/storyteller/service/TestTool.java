package tools.storyteller.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import tools.storyteller.service.StorytellerServiceGrpc;
import tools.storyteller.service.Protos.ShareStoriesRequest;
import tools.storyteller.service.Protos.Story;
import tools.storyteller.service.Protos.StoryList;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

/** Test tool for StorytellerService. */
public class TestTool {
  private final ManagedChannel channel;
  private final StorytellerServiceGrpc.StorytellerServiceBlockingStub blockingStub;

  private TestTool() {
    channel = ManagedChannelBuilder.forAddress("localhost", 8001).usePlaintext(true).build();
    blockingStub = StorytellerServiceGrpc.newBlockingStub(channel);
  }

  private void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  private void shareStories(StoryList stories) {
    final ShareStoriesRequest request =
        ShareStoriesRequest.newBuilder().setStories(stories).build();
    try {
      blockingStub.shareStories(request);
    } catch (StatusRuntimeException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    StoryList stories =
        StoryList.newBuilder().addStory(Story.newBuilder().setProject("aaa").build()).build();
    shareStories(stories);
  }

  public static void main(String[] args) {
    TestTool tool = new TestTool();
    tool.run();
  }
}
