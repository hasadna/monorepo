package hasadna.noloan.firestore;

import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class FirestoreClient {
  private final String SPAM_DOCUMENT_PATH = "noloan/smss";

  // < - 22 > - 31 for removing and changing with the user name
  private final String USER_SUGGEST_COLLECTION = "noloan/user_data/user/<username>/spam_suggestion";

  private FirebaseFirestore client;

  public FirestoreClient() {
    client = FirebaseFirestore.getInstance();
  }

  public void writeMessage(SmsMessage message) {
    FirestoreElement element = encodeMessage(message);

    // TODO add code to remove the <username> from the path and add the actual user name

    client.collection(USER_SUGGEST_COLLECTION).add(element);
  }

  public Task<DocumentSnapshot> getSpamTask() {
    DocumentReference documentReference = client.document(SPAM_DOCUMENT_PATH);
    return documentReference.get();
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

