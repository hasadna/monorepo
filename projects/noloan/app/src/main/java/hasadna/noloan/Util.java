package hasadna.noloan;

import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.firebase.DbMessages;
import hasadna.noloan.firebase.FirebaseAuthentication;
import hasadna.noloan.firebase.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;


// Don't know where to put this code
public class Util {

  // Checks if message had already been suggested. Updates counter / Adds a new suggestion
  public static void suggestMessage(SmsMessage smsMessage) {
    // Case: Message was already suggested by others but not by this user
    // 1. Add user as a "suggester"
    int index = DbMessages.getInstance().findBySms(smsMessage);
    if (index != -1) {
      SmsMessage message = DbMessages.getInstance().getMessages().get(index);
      if (!message.getSuggestersList().contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
        SmsMessage newMessage = message.toBuilder().addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId()).setId(message.getId()).build();
        new FirestoreClient().modifyMessage(message, newMessage);
      }
    }
    // Case: New suggestion
    else {
      new FirestoreClient().writeMessage(
          smsMessage.toBuilder().addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId()).build());
    }
  }


  public static void undoSuggestion(SmsMessage smsMessage) {
    // Check if user is part of the "suggesters" of this spam message
    if (FirebaseAuthentication.getInstance().containCurrentUser(smsMessage.getSuggestersList())) {
      // Case: Other people had suggested this spam as well, update just the counter (-1)
      if (smsMessage.getSuggestersCount() > 1) {
        // 1. Create new suggesters list
        List<String> newSuggesters = new ArrayList<>(smsMessage.getSuggestersList());
        newSuggesters.remove(FirebaseAuthentication.getInstance().getCurrentUserId());
        // 2. Update the counter and build modified message
        SmsMessage newMessage = SmsMessage.newBuilder()
            .addAllSuggesters(newSuggesters)
            .setId(smsMessage.getId())
            .setReceivedAt(smsMessage.getReceivedAt())
            .setSender(smsMessage.getSender())
            .setBody(smsMessage.getBody())
            .setApproved(smsMessage.getApproved())
            .build();

        new FirestoreClient().modifyMessage(smsMessage, newMessage);
      }
      // Case: User is the only one suggested this spam
      else {
        new FirestoreClient().deleteMessage(smsMessage);
      }
    }
  }
}
