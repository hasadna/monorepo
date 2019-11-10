package hasadna.noloan.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange.Type;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold smsMessages from the db, smsMessages from the inbox
public class SmsMessages {
  private static final String TAG = "SmsMessages";

  private static SmsMessages instance;
  private List<SmsMessage> dbMessages;

  private List<SmsMessage> inboxMessages;
  private ArrayList<MessagesListener> messagesListeners;

  private FirestoreClient firestoreClient;
  private FirebaseUser firebaseUser;
  private FirebaseAuth firebaseAuth;

  public SmsMessages() {
    dbMessages = new ArrayList<>();
    inboxMessages = new ArrayList<>();
    firestoreClient = new FirestoreClient();
    firebaseAuth = FirebaseAuth.getInstance();

    firebaseAuth
        .signInAnonymously()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                firebaseUser = firebaseAuth.getCurrentUser();
              } else {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
              }
            });
    messagesListeners = new ArrayList<>();
  }

  // Gets the singleton instance of SmsMessages
  public static SmsMessages get() {
    if (instance == null) {
      instance = new SmsMessages();
    }
    return instance;
  }

  // Checks if message had already been suggested. Updates counter / Adds a new suggestion
  public void suggestMessage(SmsMessage smsMessage) {
    // Case: Message was already suggested by others but not by this user
    // 1. Add user as a "suggester"
    int index = searchDbMessage(smsMessage);
    if ((index != -1)
        && !dbMessages.get(index).getSuggestersList().contains(firebaseUser.getUid())) {
      SmsMessage newMessage =
          dbMessages
              .get(index)
              .toBuilder()
              .addSuggesters(firebaseUser.getUid())
              .setId(dbMessages.get(index).getId())
              .build();
      firestoreClient.modifyMessage(dbMessages.get(index), newMessage);
      modifyMessage(newMessage);

      // Notify
      notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);

    }
    // Case: New suggestion
    else if (index == -1) {
      firestoreClient.writeMessage(
          smsMessage.toBuilder().addSuggesters(firebaseUser.getUid()).build());
    }
  }

  public void undoSuggestion(SmsMessage smsMessage) {
    // Check if user is part of the "suggesters" of this spam message
    if (smsMessage.getSuggestersList().contains(firebaseUser.getUid())) {

      // Case: Other people had suggested this spam as well, update just the counter (-1)
      if (smsMessage.getSuggestersCount() > 1) {
        // 1. Create new suggesters list
        List<String> newSuggesters = new ArrayList<>(smsMessage.getSuggestersList());
        newSuggesters.remove(firebaseUser.getUid());
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

        firestoreClient.modifyMessage(smsMessage, newMessage);
        modifyMessage(newMessage);

        // Notify
        notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }
      // Case: User is the only one suggested this spam
      else {
        removeMessage(smsMessage);
        notifyListeners(dbMessages.indexOf(smsMessage), smsMessage, Type.REMOVED);
      }
    }
  }

  // Search Db/Inbox messages by sender & body. Return -1 if no message found
  private int searchMessage(SmsMessage smsMessage, List<SmsMessage> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getBody().contentEquals(smsMessage.getBody())
          && list.get(i).getSender().contentEquals(smsMessage.getSender())) {
        return i;
      }
    }
    return -1;
  }

  // Search message by sender & body. If no message found, return -1
  public int searchDbMessage(SmsMessage smsMessage) {
    return searchMessage(smsMessage, dbMessages);
  }

  // Search message by sender & body. If no message found, return -1
  public int searchInboxMessage(SmsMessage smsMessage) {
    return searchMessage(smsMessage, inboxMessages);
  }

  // Return -1 if no message found
  public int getDbIndexById(SmsMessage smsMessage) {
    for (int i = 0; i < dbMessages.size(); i++) {
      if (dbMessages.get(i).getId().contentEquals(smsMessage.getId())) {
        return i;
      }
    }
    return -1;
  }

  // Receive changes from the db (called from FirestoreClient)
  public void updateChange(SmsMessage smsMessage, Type type) {
    switch (type) {
      case Type.ADDED:
        addMessage(smsMessage);
        break;
      case Type.MODIFIED:
        modifyMessage(smsMessage);
        break;
      case Type.REMOVED:
        removeMessage(smsMessage);
        break;
    }
  }

  public void addMessage(SmsMessage smsMessage) {
    // If spam arrives from the db - Set the received date as of the date the user had received it
    // TODO: Spams in the DB have 1 field for received date, though they are suggested by multiple
    // users which might have received the same spam - at different dates. Think wheather or not
    // this is the right representaion of a spam in the DB - Perhaps have received date field in the
    // DB be as a list of received dates.
    if (searchInboxMessage(smsMessage) != -1) {
      smsMessage =
          smsMessage
              .toBuilder()
              .setReceivedAt(inboxMessages.get(searchInboxMessage(smsMessage)).getReceivedAt())
              .build();
    }
    dbMessages.add(smsMessage);

    notifyListeners(getDbIndexById(smsMessage), smsMessage, Type.ADDED);
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = getDbIndexById(smsMessage);
    if (index != -1) {
      dbMessages.remove(index);
      firestoreClient.deleteMessage(smsMessage);
      notifyListeners(index, smsMessage, Type.REMOVED);
    } else {
      Log.w(
          TAG,
          "Attempt to remove message by its ID, but message not found. This might come from inbox trying to find a similiar spam in the db.\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    int index = getDbIndexById(newMessage);
    if (index != -1) {
      dbMessages.set(index, newMessage);
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

  public void setMessagesListener(MessagesListener messagesListener) {
    this.messagesListeners.add(messagesListener);
  }

  public void notifyListeners(int index, SmsMessage smsMessage, Type type) {
    for (MessagesListener listener : messagesListeners) {
      switch (type) {
        case Type.ADDED:
          listener.messageAdded(smsMessage);
          break;
        case Type.MODIFIED:
          listener.messageModified(index);
          break;
        case Type.REMOVED:
          listener.messageRemoved(index, smsMessage);
          break;
      }
    }
  }

  public FirebaseUser getFirebaseUser() {
    return firebaseUser;
  }

  public List<SmsMessage> getDbMessages() {
    return dbMessages;
  }

  public List<SmsMessage> getInboxMessages() {
    return inboxMessages;
  }

  public void setInboxMessages(List<SmsMessage> inboxMessages) {
    this.inboxMessages = inboxMessages;
  }

  public interface MessagesListener {

    void messageAdded(SmsMessage newMessage);

    void messageModified(int index);

    // SmsMessage parameter is passed for cases the index already had been removed from the db list
    void messageRemoved(int index, SmsMessage smsMessage);
  }
}

