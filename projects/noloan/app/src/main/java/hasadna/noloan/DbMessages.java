package hasadna.noloan;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the suggestions list
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

  public void spamModified(SmsMessage spam) {
    int index = this.spam.indexOf(spam);
    this.spam.set(index, spam);

    if (spamListener != null) spamListener.messageModified(index);
  }

  public void spamRemove(SmsMessage spam) {
    this.spam.remove(spam);

    if (spamListener != null) spamListener.messageRemoved();
  }

  public void addSuggestion(SmsMessage suggestedSpam) {
    suggestions.add(suggestedSpam);

    if (suggestionsListener != null) {
      suggestionsListener.messageAdded();
    }
  }

  public void suggestionsModified(SmsMessage suggestedSpam) {
    int index = suggestions.indexOf(suggestedSpam);
    suggestions.set(index, suggestedSpam);

    if (suggestionsListener != null) suggestionsListener.messageModified(index);
  }

  public void suggestionRemove(SmsMessage suggestedSpam) {
    suggestions.remove(suggestedSpam);

    if (suggestionsListener != null) suggestionsListener.messageRemoved();
  }

  public void setSpamListener(MessagesListener spamListener) {
    this.spamListener = spamListener;
  }

  public void setSuggestionsListener(MessagesListener suggestionsListener) {
    this.suggestionsListener = suggestionsListener;
  }

  public interface MessagesListener {
    void messageAdded();

    void messageRemoved();

    void messageModified(int index);
  }
}

