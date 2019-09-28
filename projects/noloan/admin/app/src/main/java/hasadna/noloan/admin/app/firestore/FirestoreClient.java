package hasadna.noloan.admin.app.firestore;

import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hasadna.noloan.admin.app.DBMessagesHolder;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class FirestoreClient {
  public static final String SPAM_COLLECTION_PATH = "noloan/spam/sms";

  public static final String USER_SUGGEST_COLLECTION = "noloan/spam/suggestion";

  private FirebaseFirestore client;

  public FirestoreClient() {
    client = FirebaseFirestore.getInstance();
  }

  public void writeMessage(SmsMessage message, String path) {
    FirestoreElement element = encodeMessage(message);
    client.collection(path).add(element);
  }

  public void deleteMessage(SmsMessage sms, String path) {
    client.collection(path).document(sms.getID()).delete().
        addOnSuccessListener(aVoid -> {
          Log.d("BLAAAAA","!!!!!!!!!!!!!!!!! success");
        })
        .addOnFailureListener(e -> {
          Log.d("BLAAAAA","!!!!!!!!!!!!!!!!! failed");
        });
  }

  // Start real-time listening to the DB for change, return set the result to true when done.
  public TaskCompletionSource StartListeningToMessages(String path) {

    Executor executor = Executors.newSingleThreadExecutor();
    TaskCompletionSource task = new TaskCompletionSource<>();

    CollectionReference collectionReference = client.collection(path);
    collectionReference.addSnapshotListener(
            executor,
            (queryDocumentSnapshots, e) -> {
              if (e != null) {
                return;
              }

              DBMessagesHolder messagesHolder = DBMessagesHolder.getInstance();
              for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                SmsMessage sms = null;
                try {
                  sms =
                          (SmsMessage)
                                  decodeMessage(dc.getDocument().getString("proto"), SmsMessage.newBuilder());
                } catch (InvalidProtocolBufferException e1) {
                  e1.printStackTrace();
                }
                // Spam list
                if (path.equals(SPAM_COLLECTION_PATH)) {
                  switch (dc.getType()) {
                    case ADDED:
                      messagesHolder.addSpam(sms);
                      break;
                    case MODIFIED:
                      messagesHolder.spamModified(sms);
                      break;
                    case REMOVED:
                      messagesHolder.spamRemove(sms);
                      break;
                  }
                }
                // Suggestions list
                else {
                  switch (dc.getType()) {
                    case ADDED:
                      messagesHolder.addSuggestion(sms);
                      break;
                    case MODIFIED:
                      messagesHolder.suggestionsModified(sms);
                      break;
                    case REMOVED:
                      messagesHolder.suggestionRemove(sms);
                      break;
                  }
                }
              }

              task.trySetResult(true);
            });
    return task;
  }

  // Encode user proto to base64 for storing in Firestore
  private FirestoreElement encodeMessage(SmsMessage message) {
    byte[] protoBytes = message.toByteArray();
    String base64BinaryString = Base64.encodeToString(protoBytes, Base64.DEFAULT);
    return new FirestoreElement(base64BinaryString);
  }

  private MessageLite decodeMessage(String message, MessageLite.Builder builder)
      throws InvalidProtocolBufferException {
    byte[] messageBytes = Base64.decode(message, Base64.DEFAULT);
    return builder.build().getParserForType().parseFrom(messageBytes);
  }
}

