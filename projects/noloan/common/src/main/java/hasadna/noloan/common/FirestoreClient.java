package hasadna.noloan.common;

import android.util.Base64;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class FirestoreClient {

  public static final String MESSAGES_COLLECTION_PATH = "noloan/spam/sms";

  private FirebaseFirestore client;

  public FirestoreClient() {
    client = FirebaseFirestore.getInstance();
  }

  public void writeMessage(SmsMessage message) {
    FirestoreElement element = encodeMessage(message);
    client.collection(MESSAGES_COLLECTION_PATH).add(element);
  }

  public void modifyMessage(SmsMessage oldMessage, SmsMessage newMessage) {
    DocumentReference documentReference =
        client.collection(MESSAGES_COLLECTION_PATH).document(oldMessage.getId());
    documentReference.update("proto", encodeMessage(newMessage).getProto());
  }

  public void deleteMessage(SmsMessage sms) {
    client
        .collection(MESSAGES_COLLECTION_PATH)
        .document(sms.getId())
        .delete()
        .addOnSuccessListener(aVoid -> {})
        .addOnFailureListener(e -> {});
  }

  // Start real-time listening to the DB for change, return set the result to true when done.
  public TaskCompletionSource StartListeningToMessages() {

    Executor executor = Executors.newSingleThreadExecutor();
    TaskCompletionSource task = new TaskCompletionSource<>();

    CollectionReference collectionReference = client.collection(MESSAGES_COLLECTION_PATH);
    collectionReference.addSnapshotListener(
        executor,
        (queryDocumentSnapshots, e) -> {
          if (e != null) {
            return;
          }

          DbMessages dbMessages = DbMessages.getInstance();

          // Get the message that changed
          for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
            SmsMessage sms = null;
            try {
              sms =
                  (SmsMessage)
                      decodeMessage(
                          documentChange.getDocument().getString("proto"), SmsMessage.newBuilder());
              sms = sms.toBuilder().setId(documentChange.getDocument().getId()).build();
            } catch (InvalidProtocolBufferException e1) {
              e1.printStackTrace();
            }
            switch (documentChange.getType()) {
              case ADDED:
                dbMessages.addMessage(sms);
                break;
              case MODIFIED:
                dbMessages.modifyMessage(sms);
                break;
              case REMOVED:
                dbMessages.removeMessage(sms);
                break;
            }
          }
          task.trySetResult(true);
        });
    return task;
  }

  // Encode proto to base64 for storing in Firestore
  public FirestoreElement encodeMessage(SmsMessage message) {
    byte[] protoBytes = message.toByteArray();
    String base64BinaryString = Base64.encodeToString(protoBytes, Base64.DEFAULT);
    return new FirestoreElement(base64BinaryString);
  }

  public MessageLite decodeMessage(String message, MessageLite.Builder builder)
      throws InvalidProtocolBufferException {
    byte[] messageBytes = Base64.decode(message, Base64.DEFAULT);
    return builder.build().getParserForType().parseFrom(messageBytes);
  }
}

