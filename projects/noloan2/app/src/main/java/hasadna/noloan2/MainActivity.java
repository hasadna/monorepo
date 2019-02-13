package hasadna.noloan2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hasadna.noloan2.protobuf.SMSProto.SmsMessage;
import noloan.R;

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final int PERMISSION_REQUEST_CODE = 123;

  EditText name;
  RecyclerView userView;

  FirebaseFirestore firestoreClient;
  Query query;

=======
public class MainActivity extends AppCompatActivity {
  
  TextView text;
  
  ArrayList<SmsMessage> spamList;
  
>>>>>>> origin/master
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
<<<<<<< HEAD

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
      case R.id.B_adduser: // Clicking on the add user button
        loguser user = loguser.newBuilder().setName(name.getText().toString()).build();
        writeUser(user);
    }
  }

  // Reads SMS. If no permissions are granted, exit app.
  private ArrayList<SMSmessage> readSms() {
=======
    
    text = findViewById(R.id.TV_text);
    
    spamList = new ArrayList<>();
    ArrayList<SmsMessage> messages = readSmsFromDevice();
    
    // For testing, adding the first message read to the list of spam.
    spamList.add(SmsMessage.newBuilder().setSender(messages.get(0).getSender()).setBody(messages.get(0).getBody()).build());
    
    int count = 0;
    for (SmsMessage message : messages) {
      for (SmsMessage spam : spamList) {
        if (comparingMessages(message, spam)) {
          count++;
          break;
        }
      }
    }
    
    text.setText(count + "");
  }
  
  // Basic comparing of two SMS massages
  private boolean comparingMessages(SmsMessage m1, SmsMessage m2) {
    return m1.equals(m2);
  }
  
  //Reads SMS. If no permissions are granted, exit app.
  private ArrayList<SmsMessage> readSmsFromDevice() {
>>>>>>> origin/master
    // Check for permission reading sms
    int permissionStatus = checkSelfPermission(Manifest.permission.READ_SMS);
    // If don't have permission show toast and close the app
    if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
      Log.e("ReadSms", "No permission for reading SMSs");
      Toast.makeText(this, "Permission for reading sms required", Toast.LENGTH_LONG).show();
      finish();
      return null;
    }
    return getSmsList();
  }

  // Get a list of all SMS messages in the inbox.
<<<<<<< HEAD
  private ArrayList<SMSmessage> getSmsList() {
    ArrayList<SMSmessage> smsList = new ArrayList<>();
    Cursor cursor =
        getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

    if (cursor.moveToFirst()) {
      for (int i = 0; i < cursor.getColumnCount(); i++, cursor.moveToNext()) {
        SMSmessage sms =
            SMSmessage.newBuilder()
                .setPhonenumber(cursor.getString(cursor.getColumnIndexOrThrow("address")))
                .setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")))
                .build();
=======
  private ArrayList<SmsMessage> getSmsList() {
    ArrayList<SmsMessage> smsList = new ArrayList<>();
    Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
    
    if (cursor.moveToFirst()) {
      for (int i = 0; i < cursor.getColumnCount(); i++, cursor.moveToNext()) {
        SmsMessage sms = SmsMessage.newBuilder()
          .setSender(cursor.getString(cursor.getColumnIndexOrThrow("address")))
          .setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")))
          .build();
>>>>>>> origin/master
        smsList.add(sms);
      }
    } else {
      // There are no SMS in the inbox
    }
    cursor.close();
    return smsList;
  }
}

