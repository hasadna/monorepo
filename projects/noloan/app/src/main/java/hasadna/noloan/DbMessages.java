package hasadna.noloan;

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange.Type;

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

  FirebaseUser firebaseUser;
  FirebaseAuth firebaseAuth;
  ArrayList<MessagesListener> messagesListeners;

  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  public DbMessages() {
    messages = new ArrayList<>();
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

  public void init(List<SmsMessage> messages) {
    this.messages = messages;
  }

  public List<SmsMessage> getMessages() {
    return messages;
  }

  // Checks if message had already been suggested. Updates counter / Adds a new suggestion
  public void suggestMessage(SmsMessage smsMessage) {
    // Case: Message was already suggested by others but not by this user
    // 1. Add user as a "suggester"
    int index = searchMessage(smsMessage);
    if ((index != -1) && !messages.get(index).getSuggestersList().contains(firebaseUser.getUid())) {
      SmsMessage newMessage =
          messages
              .get(index)
              .toBuilder()
              .addSuggesters(firebaseUser.getUid())
              .setId(messages.get(index).getId())
              .build();
      firestoreClient.modifyMessage(messages.get(index), newMessage);
      modifyMessage(newMessage);

      // Notify
      notifyListeners(messages.indexOf(newMessage), newMessage, Type.MODIFIED);

    }
    // Case New suggestion
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
        notifyListeners(messages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }
      // Case: User is the only one suggested this spam
      else {
        removeMessage(smsMessage);
        notifyListeners(messages.indexOf(smsMessage), smsMessage, Type.REMOVED);
      }
    }
  }

  // Search by message's sender & body. If no message found, return -1
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

  public FirebaseUser getFirebaseUser() {
    return firebaseUser;
  }

  // Receive changes from the db (called from FirestoreClient)
  public void updateChange(SmsMessage smsMessage, Type type) {
    switch (type) {
      case ADDED:
        addMessage(smsMessage);
        break;
      case MODIFIED:
        modifyMessage(smsMessage);
        break;
      case REMOVED:
        removeMessage(smsMessage);
        break;
    }
  }

  public void addMessage(SmsMessage smsMessage) {
    messages.add(smsMessage);
    notifyListeners(getIndexById(smsMessage), smsMessage, Type.ADDED);
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = getIndexById(smsMessage);
    if (index != -1) {
      messages.remove(index);
      firestoreClient.deleteMessage(smsMessage);
      notifyListeners(index, smsMessage, Type.REMOVED);
    } else {
      Log.e(
          TAG,
          "Attempt to remove message by its ID, but message not found\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    int index = getIndexById(newMessage);
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

  public void setMessagesListener(MessagesListener messagesListener) {
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

  public interface MessagesListener {

    void messageAdded(SmsMessage newMessage);

    void messageModified(int index);

    // SmsMessage parameter is passed for cases the index already had been removed from the db list
    void messageRemoved(int index, SmsMessage smsMessage);
  }
}

