package hasadna.noloan.firestore;

import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import hasadna.noloan.SpamHolder;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class FirestoreClient {
  public static final String SPAM_COLLECTION_PATH = "noloan/Spam/smss";

  public static final String USER_SUGGEST_COLLECTION = "noloan/user_data/user/<username>/spam_suggestion";

  private FirebaseFirestore client;

  public FirestoreClient() {
    client = FirebaseFirestore.getInstance();
  }

  public void writeMessage(SmsMessage message, String path) {
    FirestoreElement element = encodeMessage(message);
    client.collection(path).add(element);
  }

  public Task<QuerySnapshot> getSpamTask() {
    CollectionReference collectionReference = client.collection(SPAM_COLLECTION_PATH);
    return collectionReference.get();
  }

  public void setSpamListener() {
    CollectionReference collectionReference = client.collection(SPAM_COLLECTION_PATH);
    collectionReference.addSnapshotListener((queryDocumentSnapshots, e) ->
    {
      if (e != null)
      {
        System.out.println("failed!!!!!!!!"+e);
        return;
      }

      SpamHolder sp = SpamHolder.getInstance();
      for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
        SmsMessage sms = null;
        try {
          sms = (SmsMessage) decodeMessage(dc.getDocument().getString("proto"), SmsMessage.newBuilder());
        } catch (InvalidProtocolBufferException e1) {
          e1.printStackTrace();
        }
        switch (dc.getType()) {
          case ADDED:
            sp.add(sms);
            break;
          case MODIFIED:
            sp.modified(sms);
            break;
          case REMOVED:
            sp.remove(sms);
            break;
        }
      }
    });
  }

  // Encode user proto to base64 for storing in Firestore
  private FirestoreElement encodeMessage(SmsMessage message) {
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

