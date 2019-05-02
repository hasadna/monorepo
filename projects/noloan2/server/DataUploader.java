package hasadna.projects.noloan2.server;

import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;
import hasadna.noloan2.protobuf.SmsProto.SpamList;
import hasadna.noloan2.protobuf.SmsProto.SmsMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class DataUploader {

  private final String SPAM_DOCUMENT_PATH = "noloan/smss";

  private FileUtils fileUtils;
  private AuthService authService;

  @Inject
  DataUploader(FileUtils fileUtils, AuthService authService) {
    this.fileUtils = fileUtils;
    this.authService = authService;
  }

  void run() throws IOException {
    SmsMessage sms1 =
        SmsMessage.newBuilder()
            .setSender("mobile")
            .setBody("ההלוואה הכי מהירה שיש עד 20 אלף שח אצלך ביד, החזר רק בצקים 023750707")
            .build();
    SmsMessage sms2 =
        SmsMessage.newBuilder()
            .setSender("mobile")
            .setBody("בהמשך לפנייתך להלוואה נא לחץ על הקישור למילוי הפרטים לזרז הטיפול בפנייתך")
            .build();
    SmsMessage sms3 =
        SmsMessage.newBuilder()
            .setSender("Yeah-Atid")
            .setBody(
                "ח\"כ מיקי לוי מגיע לחיפה לדבר איתכם על כל מה שבוער במדינה. יום א' (4/3) בשעה 19:30 בפאב \"סטלה\", רחוב הירקון 1 פינת מוריה, חיפה. להרשמה ופרטים נוספים: https://goo.gl/tzTVPc  להסרה: https://goo.gl/QjbrTs023750707")
            .build();
    SpamList spam = SpamList.newBuilder().addSms(sms1).addSms(sms2).addSms(sms3).build();

    fileUtils.writePrototxt(spam, "spam.Prototxt");

    authService.refreshToken();
    FirestoreProtoClient client = new FirestoreProtoClient(authService.getProjectId(), authService.getToken());
    SpamList spam = (SpamList) fileUtils.readPrototxt("Spam.prototxt", SpamList.newBuilder());
    List<SmsMessage> SMSList = spam.getSms();
    client.setProtoDocument(SPAM_DOCUMENT_PATH, spam);
  }

  @Singleton
  @Component(modules = CommonModule.class)
  interface ToolComponent {
    DataUploader getTool();
  }

  public static void main(String[] args) throws IOException {
    Flags.parseCurrentPackage(args);
    DaggerDataUploader_ToolComponent.create().getTool().run();
  }
}

