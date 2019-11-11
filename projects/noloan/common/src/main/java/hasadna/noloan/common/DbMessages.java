package hasadna.noloan.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold smsMessages from the db, smsMessages from the inbox
public class DbMessages {
  private static final String TAG = "DbMessages";

  enum Type {ADDED, MODIFIED, REMOVED}

  private static DbMessages instance;
  private List<SmsMessage> messages;

  private ArrayList<MessagesListener> messagesListeners;

  private FirestoreClient client;

  public DbMessages() {
    messages = new ArrayList<>();
    client = new FirestoreClient();
    messagesListeners = new ArrayList<>();
  }

  // Gets the singleton instance of hasadna.noloan.common.DbMessages
  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  //add message to the DB, if already in the DB suggest it.
  //TODO test...
  public void addMessage(SmsMessage smsMessage) {
    int index =findSms(smsMessage, messages);
    if (index != -1) {
      // if the message is in the DB suggest it
      SmsMessage message = messages.get(index);
      if(message.getSuggestersList().contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
        SmsMessage newMessage =
            message.toBuilder().addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId()).build();

        modifyMessage(newMessage);
      }
    } else {
      // if the message not in the DB, add me as the only suggester and add the message
      SmsMessage message = smsMessage.toBuilder().addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId()).build();
      messages.add(message);
      client.writeMessage(message);
    }
    notifyListeners(findSms(smsMessage, messages), smsMessage, Type.ADDED);
  }

  //undo suggestion if suggested already, if last to undo remove form the DB.
  //TODO test...
  public void removeMessage(SmsMessage smsMessage) {
    int index = findSms(smsMessage, messages);
    if (index != -1) {

      // Check if user is part of the "suggesters" of this spam message
      if (smsMessage.getSuggestersList().contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {

        // Case: Other people had suggested this spam as well, update just the counter (-1)
        if (smsMessage.getSuggestersCount() > 1) {
          // 1. Create new suggesters list
          List<String> newSuggesters = new ArrayList<>(smsMessage.getSuggestersList());
          newSuggesters.remove(FirebaseAuthentication.getInstance().getCurrentUserId());
          // 2. Update the counter and build modified message
          SmsMessage newMessage =
              SmsMessage.newBuilder()
                  .addAllSuggesters(newSuggesters)
                  .setId(smsMessage.getId())
                  .setReceivedAt(smsMessage.getReceivedAt())
                  .setSender(smsMessage.getSender())
                  .setBody(smsMessage.getBody())
                  .setApproved(smsMessage.getApproved())
                  .build();

          modifyMessage(newMessage);
        }
        // Case: User is the only one suggested this spam
        else {
          if (!smsMessage.getApproved()) { //TODO make sure the user is admin
            // if the sms was approved by an admin it will not be deleted. only admin can delete approved spam.
            messages.remove(smsMessage);
            client.deleteMessage(smsMessage);
            notifyListeners(messages.indexOf(smsMessage), smsMessage, Type.REMOVED);
          } else {
            Log.e(TAG, "user try to remove approved spam");
          }
        }
      } else {
        Log.e(TAG, "remove something that the user didn't suggest");
      }
    } else {
      Log.w(
          TAG,
          "Attempt to remove message by its ID, but message not found. This might come from inbox trying to find a similiar spam in the db.\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    int index = findSms(newMessage,messages);
    if (index != -1) {
      SmsMessage oldMessage = messages.get(index);
      messages.set(index, newMessage);
      client.modifyMessage(oldMessage, newMessage);
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
    this.messagesListeners.add(messagesListener);
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

  public List<SmsMessage> getMessages() {
    return messages;
  }


  public interface MessagesListener {

    void messageAdded(SmsMessage newMessage);

    void messageModified(int index);

    // SmsMessage parameter is passed for cases the index already had been removed from the db list
    void messageRemoved(int index, SmsMessage smsMessage);

  }

  // static function to search a given list
  public static int findSms(SmsMessage sms, List<SmsMessage> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getBody().contentEquals(sms.getBody())
          && list.get(i).getSender().contentEquals(sms.getSender())) {
        return i;
      }
    }
    return -1;
  }
}

