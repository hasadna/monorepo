package hasadna.noloan.admin.app;

import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the spam list
public class SpamHolder {
  private static SpamHolder instance;
  private List<SmsMessage> spam;

  public static SpamHolder getInstance() {
    if (instance == null) {
      instance = new SpamHolder();
    }
    return instance;
  }

  public List<SmsMessage> getSpam() {
    return spam;
  }

  public void setSpam(List<SmsMessage> spam) {
    this.spam = spam;
  }
}

