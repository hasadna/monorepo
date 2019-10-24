package hasadna.noloan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.google.protobuf.MessageLite;

import hasadna.noloan.protobuf.LawsuitProto.Lawsuit;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class AppSharedPreferences extends AppCompatActivity {

  static Context context;

  public AppSharedPreferences() {
    context = getApplicationContext();
  }

  public static void updateSharedPreferences(SmsMessage smsMessage) {
    SharedPreferences.Editor editor =
        context
            .getSharedPreferences(
                context.getPackageName() + R.string.lawsuits_sharedPreferences_path, MODE_PRIVATE)
            .edit();
    editor.putString(
        getKey(smsMessage), Base64.encodeToString(smsMessage.toByteArray(), Base64.DEFAULT));
    editor.apply();
  }

  public static Lawsuit getSharedPreferencesLawsuit(SmsMessage smsMessage) {
    // Key of the message in the sharedPreferences
    String sharedPreferencesKey = getKey(smsMessage);

    // Check if exists
    if (context
        .getSharedPreferences(
            context.getPackageName() + context.getString(R.string.lawsuits_sharedPreferences_path),
            MODE_PRIVATE)
        .contains(sharedPreferencesKey)) {
      byte[] messageBytes =
          Base64.decode(
              context
                  .getSharedPreferences(
                      context.getPackageName() + R.string.lawsuits_sharedPreferences_path,
                      MODE_PRIVATE)
                  .getString(sharedPreferencesKey, "Key not found"),
              Base64.DEFAULT);
      try {
        return Lawsuit.newBuilder().build().getParserForType().parseFrom(messageBytes);
      } catch (Exception e) {
        Log.e(TAG, "Error parsing Lawsuit.Proto from the sharedPreferences\n" + e.getMessage());
      }
    }
    return null;
  }

  // Return generated key out of message's: Sender + Body + Date received
  public static String getKey(SmsMessage smsMessage) {
    return String.valueOf(
        (smsMessage.getSender() + smsMessage.getBody() + smsMessage.getReceivedAt()).hashCode());
  }
}

