package hasadna.noloan2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.galgo.noloan.protobuf.UserProto.loguser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import noloan.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  
  EditText name;
  RecyclerView userView;
  
  FirebaseFirestore firestoreClient;
  Query query;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    initFirestore();
    
    name = findViewById(R.id.ET_name);
    
    Button add = findViewById(R.id.B_adduser);
    add.setOnClickListener(this);
    
    userView = findViewById(R.id.RV_userview);
    UserAdapter adapter = new UserAdapter(query);
    userView.setAdapter(adapter);
    userView.setLayoutManager(new LinearLayoutManager(this));
    adapter.startListening();
  }
  
  private void initFirestore() {
    firestoreClient = FirebaseFirestore.getInstance();
    query = firestoreClient.collection("Users");
  }
  
  // Write the user to the Firestore
  private void writeUser(loguser user) {
    FirestoreElement userElement = encodeUser(user);
    
    CollectionReference users = firestoreClient.collection("Users");
    users.add(userElement);
  }
  
  // Encode user proto to base64 for storing in Firestore
  private FirestoreElement encodeUser(loguser user) {
    byte[] protoBytes = user.toByteArray();
    String base64BinaryString = Base64.encodeToString(protoBytes, Base64.DEFAULT);
    return new FirestoreElement(base64BinaryString);
  }
  
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.B_adduser:
        loguser user = loguser.newBuilder()
          .setName(name.getText().toString())
          .build();
        writeUser(user);
    }
  }
}
