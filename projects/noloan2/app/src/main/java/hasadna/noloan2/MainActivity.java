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

public class MainActivity extends AppCompatActivity {

  TextView text;

  ArrayList<SmsMessage> spamList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    text = findViewById(R.id.TV_text);

    spamList = new ArrayList<>();
    ArrayList<SmsMessage> messages = readSmsFromDevice();

    // For testing, adding the first message read to the list of spam.
    spamList.add(
        SmsMessage.newBuilder()
            .setSender(messages.get(0).getSender())
            .setBody(messages.get(0).getBody())
            .build());

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

  // Reads SMS. If no permissions are granted, exit app.
  private ArrayList<SmsMessage> readSmsFromDevice() {
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
  private ArrayList<SmsMessage> getSmsList() {
    ArrayList<SmsMessage> smsList = new ArrayList<>();
    Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

    if (cursor.moveToFirst()) {
      for (int i = 0; i < cursor.getColumnCount(); i++, cursor.moveToNext()) {
        SmsMessage sms =
            SmsMessage.newBuilder()
                .setSender(cursor.getString(cursor.getColumnIndexOrThrow("address")))
                .setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")))
                .build();
        smsList.add(sms);
      }
    } else {
      // There are no SMS in the inbox
    }
    cursor.close();
    return smsList;
  }
}

