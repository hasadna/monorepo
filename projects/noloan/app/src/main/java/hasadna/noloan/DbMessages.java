package hasadna.noloan;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the messages from the db
public class DbMessages {
  private static final String TAG = "DbMessages";

  private static DbMessages instance;
  private List<SmsMessage> messages;

  FirestoreClient firestoreClient;
  MessagesListener messagesListener;

  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  public DbMessages() {
    messages = new ArrayList<>();
    firestoreClient = new FirestoreClient();
  }

  public void init(List<SmsMessage> messages) {
    this.messages = messages;
  }

  public List<SmsMessage> getMessages() {
    return messages;
  }

  // Checks if message had already been suggested. Updates counter / Adds a new suggestion
  public void suggestMessage(SmsMessage smsMessage) {

    // Search if message was already suggested -> Modify counter (+1)
    int index = searchMessage(smsMessage);
    if (index != -1) {
      SmsMessage newMessage =
          messages.get(index).toBuilder().setCounter(messages.get(index).getCounter() + 1).build();
      firestoreClient.modifyMessage(messages.get(index), newMessage);
      modifyMessage(newMessage);
      if (messagesListener != null) {
        messagesListener.messageModified(index);
      }
    }
    // Add new suggestion
    else {
      firestoreClient.writeMessage(
          smsMessage.toBuilder().setCounter(smsMessage.getCounter() + 1).build());
    }
  }

  public void undoSuggestion(SmsMessage smsMessage) {

    // If other people suggested this message as well - Modify message with a new counter (-1)
    if (smsMessage.getCounter() > 1) {
      SmsMessage newMessage =
          smsMessage.toBuilder().setCounter(smsMessage.getCounter() - 1).build();
      firestoreClient.modifyMessage(smsMessage, newMessage);
      modifyMessage(newMessage);
      if (messagesListener != null) {
        messagesListener.messageModified(messages.indexOf(smsMessage));
      }
    }

    // In case the user is the only one suggested this spam - Remove suggestion
    else {
      removeMessage(smsMessage);
    }
  }

  // Search for message with same body & sender. If none found, return -1
  public int searchMessage(SmsMessage smsMessage) {
    for (int i = 0; i < messages.size(); i++) {
      if (messages.get(i).getBody().contentEquals(smsMessage.getBody())
          && messages.get(i).getSender().contentEquals(smsMessage.getSender())) {
        return i;
      }
    }
    return -1;
  }

  // Return -1 if no message found
  public int getIndexById(SmsMessage smsMessage) {
    for (int i = 0; i < messages.size(); i++) {
      if (messages.get(i).getId().contentEquals(smsMessage.getId())) {
        return i;
      }
    }
    return -1;
  }

  // region Functions used by db Listeners when list changes
  public void addMessage(SmsMessage smsMessage) {
    messages.add(smsMessage);
    if (messagesListener != null) {
      messagesListener.messageAdded();
    }
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = getIndexById(smsMessage);
    if (index != -1) {
      messages.remove(smsMessage);
      firestoreClient.deleteMessage(smsMessage);
      if (messagesListener != null) {
        messagesListener.messageRemoved(index);
      }
    } else {
      Log.e(
          TAG,
          "Attempt to remove message by its ID, but message not found\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    // Find the existing message in the list - modify
    if (getIndexById(newMessage) != -1) {
      messages.set(getIndexById(newMessage), newMessage);
      if (messagesListener != null) {
        messagesListener.messageModified(getIndexById(newMessage));
      }
    }
  }
  // endregion

  public void setMessagesListener(MessagesListener messagesListener) {
    this.messagesListener = messagesListener;
  }

  public interface MessagesListener {

    void messageAdded();

    void messageModified(int index);

    void messageRemoved(int index);
  }
}

