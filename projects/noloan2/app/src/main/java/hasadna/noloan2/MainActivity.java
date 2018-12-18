package hasadna.noloan2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import hasadna.noloan2.protobuf.SmsProto.SMSmessage;
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
    userView.setAdapter(new SmsAdapter(readSms()));
    // Firestore test
    /*UserAdapter adapter = new UserAdapter(query);
    userView.setAdapter(adapter);
    adapter.startListening();*/
    userView.setLayoutManager(new LinearLayoutManager(this));
    
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
  
  //this work if already have permission
  private ArrayList<SMSmessage> readSms() {
    // Check for permission reading sms
    int hasPermission = checkSelfPermission(Manifest.permission.READ_SMS);
    if (hasPermission != PackageManager.PERMISSION_GRANTED) // If don't have permission show permission request
    {
      requestPermissions(new String[]{Manifest.permission.READ_SMS}, 11); // Request code is the code for onRequestPermissionsResult
      return null;
    }
    
    return getSmsList();
  }
  
  private ArrayList<SMSmessage> getSmsList() {
    ArrayList<SMSmessage> smsList = new ArrayList<>();
    Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
    
    if (cursor.moveToFirst()) {
      for (int i = 0; i < cursor.getColumnCount(); i++, cursor.moveToNext()) {
        SMSmessage sms = SMSmessage.newBuilder()
          .setPhonenumber(cursor.getString(cursor.getColumnIndexOrThrow("address")))
          .setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")))
          .build();
        smsList.add(sms);
      }
    } else {
      // There are no SMS in the inbox
    }
    cursor.close();
    return smsList;
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case 11:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          getSmsList(); // Permission granted, now i need somehow to get the list to the adapter...
        } else {
          // Permission denied
        }
        
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
