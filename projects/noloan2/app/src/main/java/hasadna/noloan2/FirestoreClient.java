package hasadna.noloan2;

import android.util.Base64;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.InvalidProtocolBufferException;

import hasadna.noloan2.protobuf.SMSProto.SpamList;
import hasadna.noloan2.protobuf.SMSProto.SmsMessage;

import com.google.protobuf.MessageLite;


public class FirestoreClient {
  private final String SPAM_DOCUMENT_PATH = "noloan/smss";
  
  // < - 22 > - 31 for removing and changing with the user name
  private final String USER_SUGGEST_COLLECTION = "noloan/user_data/user/<username>/spam_suggestions";
  
  private FirebaseFirestore client;
  
  public FirestoreClient() {
    client = FirebaseFirestore.getInstance();
  }
  
  // Write the message to the Firestore
  public void writeMessage(SmsMessage message) {
    FirestoreElement element = encodeMessage(message);
    
    // TODO add code to remove the <username> from the path and add the actual user name
    
    client.collection(USER_SUGGEST_COLLECTION).add(element);
  }
  
  DocumentSnapshot snapshot;
  
  public SpamList getSpam() {
    DocumentReference document = client.document(SPAM_DOCUMENT_PATH);
    
    // Fix for the busy waiting don't work
    //Unable to start activity ComponentInfo{hasadna.noloan2/hasadna.noloan2.MainActivity}: java.lang.IllegalStateException: Must not be called on the main application thread
    /*
    Task<DocumentSnapshot> task = document.get();
    task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
          snapshot = task.getResult();
        } else {
          Log.e("Firesotre", "Failed to get snapsot from the Firestore");
        }
      }
    });
    try {
      snapshot = Tasks.await(task);
    } catch (ExecutionException | InterruptedException e) {
      Log.e("Firesotre", "Wait interrupted");
    }
    SpamList spam = null;
    if (snapshot != null) {
      spam = decodeSpam(snapshot.toObject(FirestoreElement.class));
    }
    */
    
    Task<DocumentSnapshot> task = document.get();
    while (!task.isComplete()) {
    }
    DocumentSnapshot result = task.getResult();
    SpamList spam = null;
    try {
      spam = (SpamList) decodeMessage(result.getString("proto"), SpamList.Builder);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    return spam;
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
