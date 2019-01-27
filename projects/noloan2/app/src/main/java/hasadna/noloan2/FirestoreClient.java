package hasadna.noloan2;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hasadna.noloan2.protobuf.SMSProto.SmsMessage;


public class FirestoreClient {
  private final String SMS_MESSAGE_COLLECTION = "noloan/smss";
  
  // < - 22 > - 31 for removing and changing with the user name
  private final String USER_SUGGEST_COLLECTION = "noloan/user_data/user/<username>/spam_suggestions";
  
  private CollectionReference collection;
  
  public FirestoreClient() {
    collection = FirebaseFirestore.getInstance().collection(SMS_MESSAGE_COLLECTION);
  }
  
  // Write the message to the Firestore
  public void writeMessage(SmsMessage message) {
    FirestoreElement element = encodeMessage(message);
    
    // TODO add code to remove the <username> from the path and add the actual user name
    
    FirebaseFirestore.getInstance().collection(USER_SUGGEST_COLLECTION).add(element);
  }
  
  QuerySnapshot snapshot;
  
  public ArrayList<SmsMessage> getMessages() {
    ArrayList<SmsMessage> results = new ArrayList<>();
    
    Task<QuerySnapshot> task = collection.get();
    task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
  
    if (snapshot != null) {
      List<DocumentSnapshot> list = snapshot.getDocuments();
      for (DocumentSnapshot document : list) {
        FirestoreElement element = document.toObject(FirestoreElement.class);
        results.add(decodeMessage(element));
      }
      return results;
    } else {
      // onComplete failed
      return null;
    }
  }
  
  // Encode user proto to base64 for storing in Firestore
  private FirestoreElement encodeMessage(SmsMessage message) {
    byte[] protoBytes = message.toByteArray();
    String base64BinaryString = Base64.encodeToString(protoBytes, Base64.DEFAULT);
    return new FirestoreElement(base64BinaryString);
  }
  
  private SmsMessage decodeMessage(FirestoreElement element) {
    byte[] messageBytes = Base64.decode(element.getBase64(), Base64.DEFAULT);
    SmsMessage message = null;
    try {
      message = SmsMessage.getDefaultInstance().getParserForType().parseFrom(messageBytes);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    return message;
  }
}
