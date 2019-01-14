package hasadna.noloan2;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan2.protobuf.SMSProto.SmsMessage;


public class FirestoreClient {
  FirebaseFirestore client;
  
  String collection;
  
  public FirestoreClient(String collection) {
    this.collection = collection;
    client = FirebaseFirestore.getInstance();
  }
  
  // Write the message to the Firestore
  public void writeMassage(SmsMessage message) {
    FirestoreElement element = encodeMessage(message);
    client.collection(collection).add(element);
  }
  
  QuerySnapshot snapshot;
  
  public ArrayList<SmsMessage> getMessages() {
    ArrayList<SmsMessage> results = new ArrayList<>();
    
    client.collection(collection).get()
      .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()) {
            snapshot = task.getResult();
          } else {
            //try again later
          }
        }
      });
    
    // wait for the results
    // TODO limit the time it take
    while (snapshot == null) {
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
  
  public SmsMessage decodeMessage(FirestoreElement element) {
    byte[] userbyte = Base64.decode(element.getBase64(), Base64.DEFAULT);
    SmsMessage message = null;
    try {
      message = message.newBuilder().build().getParserForType().parseFrom(userbyte);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    return message;
  }
}
