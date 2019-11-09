package hasadna.noloan.firebase;

import android.util.Log;

import java.util.ArrayList;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold spam from the db
public class DbMessages {
  private static final String TAG = "DbMessages";

  private enum Type {ADDED, REMOVED, MODIFIED}

  private static DbMessages instance;

  private ArrayList<SmsMessage> messages;
  private ArrayList<MessagesListener> messagesListeners;

  public DbMessages() {
    messages = new ArrayList<>();
    messagesListeners = new ArrayList<>();
  }

  // Gets the singleton instance of DbMessages
  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  // Search messages by sender & body. Return -1 if no message found
  public int findBySms(SmsMessage smsMessage) {
    return findSms(smsMessage, messages);
  }

  // Return -1 if no message found
  public int findById(SmsMessage smsMessage) {
    return findSmsById(smsMessage, messages);
  }

  public void addMessage(SmsMessage smsMessage) {
    messages.add(smsMessage);
    notifyListeners(findById(smsMessage), smsMessage, Type.ADDED);
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = findById(smsMessage);
    if (index != -1) {
      messages.remove(index);
      notifyListeners(index, smsMessage, Type.REMOVED);
    } else {
      Log.w(
          TAG,
          "Attempt to remove message by its ID, but message not found. This might come from inbox trying to find a similiar spam in the db.\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    int index = findById(newMessage);
    if (index != -1) {
      messages.set(index, newMessage);
      notifyListeners(index, newMessage, Type.MODIFIED);
    } else {
      Log.e(
          TAG,
          "Attempt to modify message but message not found.\n Message id: "
              + newMessage.getId()
              + "\nBody: "
              + newMessage.getBody()
              + "\nSender: "
              + newMessage.getSender());
    }
  }

  public void addMessagesListener(MessagesListener messagesListener) {
    messagesListeners.add(messagesListener);
  }


  public void notifyListeners(int index, SmsMessage smsMessage, Type type) {
    for (MessagesListener listener : messagesListeners) {
      switch (type) {
        case ADDED:
          listener.messageAdded(smsMessage);
          break;
        case MODIFIED:
          listener.messageModified(index);
          break;
        case REMOVED:
          listener.messageRemoved(index, smsMessage);
          break;
      }
    }
  }

  public ArrayList<SmsMessage> getMessages() {
    return messages;
  }

  public interface MessagesListener {

    void messageAdded(SmsMessage smsMessage);

    void messageModified(int index);

    // SmsMessage parameter is passed for cases the index already had been removed from the db list
    void messageRemoved(int index, SmsMessage smsMessage);
  }

  // static function to search a given list
  public static int findSms(SmsMessage sms, ArrayList<SmsMessage> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getBody().contentEquals(sms.getBody())
          && list.get(i).getSender().contentEquals(sms.getSender())) {
        return i;
      }
    }
    return -1;
  }

  public static int findSmsById(SmsMessage sms, ArrayList<SmsMessage> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getId().contentEquals(sms.getId())) {
        return i;
      }
    }
    return -1;
  }

}

