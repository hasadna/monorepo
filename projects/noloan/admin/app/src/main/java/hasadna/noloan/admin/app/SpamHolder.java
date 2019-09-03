package hasadna.noloan.admin.app;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the spam list
public class SpamHolder {
  private static SpamHolder instance;
  private List<SmsMessage> spam;

  SpamListener listener;

  public static SpamHolder getInstance() {
    if (instance == null) {
      instance = new SpamHolder();
    }
    return instance;
  }

  public SpamHolder() {
    spam = new ArrayList<>();
  }

  public void init(List<SmsMessage> spam) {
    this.spam = spam;
  }

  public List<SmsMessage> getSpam() {
    return spam;
  }

  public void add(SmsMessage sms) {
    spam.add(sms);

    if (listener != null) {
      listener.spamAdded();
    }
  }

  public void modified(SmsMessage sms) {
    int index = spam.indexOf(sms);
    spam.set(index, sms);

    if (listener != null) listener.spamModified(index);
  }

  public void remove(SmsMessage sms) {
    spam.remove(sms);

    if (listener != null) listener.spamRemoved();
  }

  public void setSpamListener(SpamListener listener) {
    this.listener = listener;
  }

  public interface SpamListener {
    void spamAdded();

    void spamRemoved();

    void spamModified(int index);
  }
}

