package hasadna.noloan;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the spam and suggestions lists
public class DbMessages {
  private static DbMessages instance;
  private List<SmsMessage> spam;
  private List<SmsMessage> suggestions;

  MessagesListener spamListener;
  MessagesListener suggestionsListener;

  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  public DbMessages() {
    spam = new ArrayList<>();
    suggestions = new ArrayList<>();
  }

  public void init(List<SmsMessage> spam, List<SmsMessage> suggestions) {
    this.spam = spam;
    this.suggestions = suggestions;
  }

  public List<SmsMessage> getSpam() {
    return spam;
  }

  public List<SmsMessage> getSuggestions() {
    return suggestions;
  }

  public void addSpam(SmsMessage spam) {
    this.spam.add(spam);

    if (spamListener != null) {
      spamListener.messageAdded();
    }
  }

  public void spamRemove(SmsMessage spam) {
    int index = this.spam.indexOf(spam);
    this.spam.remove(spam);

    if (spamListener != null) {
      spamListener.messageRemoved(index);
    }
  }

  public void addSuggestion(SmsMessage suggestedSpam) {
    suggestions.add(suggestedSpam);

    if (suggestionsListener != null) {
      suggestionsListener.messageAdded();
    }
  }

  public void suggestionRemove(SmsMessage suggestedSpam) {
    int index = suggestions.indexOf(suggestedSpam);
    suggestions.remove(suggestedSpam);

    if (suggestionsListener != null) {
      suggestionsListener.messageRemoved(index);
    }
  }

  public void setSpamListener(MessagesListener spamListener) {
    this.spamListener = spamListener;
  }

  public void setSuggestionsListener(MessagesListener suggestionsListener) {
    this.suggestionsListener = suggestionsListener;
  }

  public interface MessagesListener {

    void messageAdded();

    void messageRemoved(int index);
  }
}

