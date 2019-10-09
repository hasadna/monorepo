package hasadna.noloan;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

// Simple singleton to hold the messages from the db
public class DbMessages {
  private static DbMessages instance;
  private List<SmsMessage> messages;

  FirestoreClient firestoreClient;
  MessagesListener messagesListener;

  public static DbMessages getInstance() {
    if (instance == null) {
      instance = new DbMessages();
    }
    return instance;
  }

  public DbMessages() {
    messages = new ArrayList<>();
    firestoreClient = new FirestoreClient();
  }

  public void init(List<SmsMessage> messages) {
    this.messages = messages;
  }

  public List<SmsMessage> getMessages() {
    return messages;
  }

  // Checks if message had already been suggested. Updates counter / Adds a new suggestion
  public void suggestMessage(SmsMessage sms){

    // Search if message was already suggested -> Modify counter (+1)
    SmsMessage message = searchSuggestions(sms.getBody(),sms.getSender());
    if(message != null){
      int index  = messages.indexOf(message);
      SmsMessage newMessage = message.toBuilder().setCounter(message.getCounter()+1).build();
      firestoreClient.modifyMessage(message,newMessage);
      modifyMessage(newMessage);
      if(messagesListener!=null){
        messagesListener.messageModified(index);
      }
    }

    // Add new suggestion
    else{
      firestoreClient.writeMessage(sms.toBuilder().setCounter(sms.getCounter()+1).build());
    }
  }

  public void undoSuggestion(SmsMessage sms){

    // If other people suggested this message as well - Modify message with a new counter (-1)
    if(sms.getCounter()>1){
      SmsMessage newMessage = sms.toBuilder().setCounter(sms.getCounter()-1).build();
      firestoreClient.modifyMessage(sms,newMessage);
      modifyMessage(newMessage);
      if(messagesListener!=null){
        messagesListener.messageModified(messages.indexOf(sms));
      }
    }

    // In case the user is the only one suggested this spam - Remove suggestion
    else{
      removeMessage(sms);
    }
  }

  public SmsMessage searchSuggestions(String body, String sender){
    SmsMessage sms = null;
    for (SmsMessage message: messages) {
      if(message.getBody().contentEquals(body) && message.getSender().contentEquals(sender)){
        sms = message;
      }
    }

    return sms;
  }

  //region Functions used by db Listeners when list changes
  public void addMessage(SmsMessage smsMessage) {
    messages.add(smsMessage);
    if (messagesListener != null) {
      messagesListener.messageAdded();
    }
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = messages.indexOf(smsMessage);
    messages.remove(smsMessage);
    firestoreClient.deleteMessage(smsMessage);
    if (messagesListener != null) {
      messagesListener.messageRemoved(index);
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    // Find the index of the message that will be modified (By its ID)
    int index=0;
    for(SmsMessage message: messages){
      if(message.getId().contentEquals(newMessage.getId())){
        index = messages.indexOf(message);
        }
    }

    // Modify message & notify Listeners
    messages.set(index, newMessage);
    if (messagesListener != null) messagesListener.messageModified(index);
  }
  //endregion

  public void setMessagesListener(MessagesListener messagesListener) {
    this.messagesListener = messagesListener;
  }

  public interface MessagesListener {

    void messageAdded();

    void messageModified(int index);

    void messageRemoved(int index);
  }
}