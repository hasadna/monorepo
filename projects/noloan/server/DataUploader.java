package hasadna.projects.noloan.server;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.firestore.FirestoreProtoClient;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import hasadna.noloan.protobuf.SmsProto.SpamList;

import java.io.IOException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataUploader {

  private static final String SPAM_DOCUMENT_PATH = "noloan/smss";

  private FileUtils fileUtils;
  private AuthService authService;

  @Inject
  DataUploader(FileUtils fileUtils, AuthService authService) {
    this.fileUtils = fileUtils;
    this.authService = authService;
  }

  void run() throws IOException {
    authService.refreshToken();

    FirestoreProtoClient client =
        new FirestoreProtoClient(authService.getProjectId(), authService.getToken());

    SpamList spam =
        (SpamList)
            fileUtils.readPrototxt("projects/noloan/server/spam.prototxt", SpamList.newBuilder());

    client.setProtoDocument(SPAM_DOCUMENT_PATH, spam);
  }

  @Singleton
  @Component(modules = CommonModule.class)
  interface ToolComponent {
    DataUploader getTool();
  }

  public static void main(String[] args) throws IOException {
    Flags.parse(args, AuthService.class.getPackage());
    DaggerDataUploader_ToolComponent.create().getTool().run();
  }
}

