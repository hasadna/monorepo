package projects.noloan.app.filter;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Resources;
import com.google.common.collect.ImmutableList;
import projects.noloan.app.Protos.SmsMessage;
import projects.noloan.app.Protos.SmsMessageList;
import com.google.protobuf.TextFormat;
import com.google.protobuf.MessageLite;


/* Filter for spam SMS messages */
public class SpamFilter {
  private static final String SPAM_PROTOTXT = "projects/noloan/app/filter/spam.prototxt";

  ImmutableList<SmsMessage> spam;

  public SpamFilter() {
    readSpamPrototxt();
  }

  private void readSpamPrototxt() {
    try {
      String text = Resources.toString(Resources.getResource(SPAM_PROTOTXT), UTF_8);
      SmsMessageList.Builder builder = SmsMessageList.newBuilder();
      TextFormat.merge(text, builder);
      spam = ImmutableList.copyOf(builder.build().getMessageList());
    } catch (Exception e) {
        throw new RuntimeException(e);
    }    
  }

  public boolean isSpam(SmsMessage message) {
    // TODO: Implement simple Naive Bayes spam filter
    return true;
  }

  /* Returns messages that are spam */
  public ImmutableList<SmsMessage> filter(ImmutableList<SmsMessage> messages) {
    ImmutableList.Builder<SmsMessage> result = ImmutableList.builder();
    for (SmsMessage message : messages) {
      if (isSpam(message)) {
        result.add(message);
      }
    }
    return result.build();
  }
}
