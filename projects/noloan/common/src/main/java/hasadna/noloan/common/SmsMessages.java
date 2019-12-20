package hasadna.noloan.common;

import android.util.Log;

import com.google.firebase.firestore.DocumentChange.Type;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import hasadna.noloan.protobuf.CompanyProto.Company;

// Simple singleton to hold smsMessages from the db, smsMessages from the inbox
public class SmsMessages {
  private static final String TAG = "common.SmsMessages";

  private static SmsMessages instance;
  private List<SmsMessage> dbMessages;

  private List<SmsMessage> inboxMessages;
  private ArrayList<MessagesListener> messagesListeners;

  private FirestoreClient firestoreClient;

  public SmsMessages() {
    dbMessages = new ArrayList<>();
    inboxMessages = new ArrayList<>();
    firestoreClient = new FirestoreClient();
    messagesListeners = new ArrayList<>();
  }

  // Gets the singleton instance of hasadna.noloan.common.SmsMessages
  public static SmsMessages get() {
    if (instance == null) {
      instance = new SmsMessages();
    }
    return instance;
  }

  // Checks if message had already been suggested. Add user as a suggester / Adds a new suggestion
  public void suggestMessage(SmsMessage smsMessage) {
    // Case: Message was already suggested by others but not by this user
    // 1. Add user as a "suggester" of this SMS
    int index = searchDbMessage(smsMessage);
    if ((index != -1)
        && !dbMessages
            .get(index)
            .getSuggestersList()
            .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
      SmsMessage newMessage =
          dbMessages
              .get(index)
              .toBuilder()
              .addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId())
              .setId(dbMessages.get(index).getId())
              .build();
      firestoreClient.modifyMessage(dbMessages.get(index), newMessage);
      modifyMessage(newMessage);

      // Notify
      notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
    }

    // Case: New suggestion
    else if (index == -1) {
      firestoreClient.writeMessage(
          smsMessage
              .toBuilder()
              .addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId())
              .build());
    }
  }

  // Suggest a company for a given SMS, if message isn't in the DB do nothing.
  public void suggestCompany(SmsMessage smsMessage, Company company) {

    // Fetch spam message from DB
    int index = searchDbMessage(smsMessage);
    if (index != -1) {
      Company newCompany;
      SmsMessage newMessage;

      // Check if this company was already suggested for this spam
      int companyIndex = searchCompany(company, dbMessages.get(index).getCompanyList());

      // Case: New company suggestion for this spam
      if (companyIndex == -1) {
        // 1. Add user as a suggester for this company
        newCompany =
            company
                .toBuilder()
                .addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId())
                .buildPartial();
        // 2. Add Company suggestion to the SmsMessage
        newMessage = dbMessages.get(index).toBuilder().addCompany(newCompany).build();

        // Push to DB
        firestoreClient.modifyMessage(dbMessages.get(index), newMessage);
        modifyMessage(newMessage);

        // Notify
        notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }

      // Case: Company was already suggested for this spam
      // If user isn't in the company's suggesters list - Add
      else if (!dbMessages
          .get(index)
          .getCompanyList()
          .get(companyIndex)
          .getSuggestersList()
          .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
        newCompany =
            dbMessages
                .get(index)
                .getCompanyList()
                .get(companyIndex)
                .toBuilder()
                .addSuggesters(FirebaseAuthentication.getInstance().getCurrentUserId())
                .build();
        newMessage = dbMessages.get(index).toBuilder().setCompany(companyIndex, newCompany).build();

        // Push to DB
        firestoreClient.modifyMessage(dbMessages.get(index), newMessage);
        modifyMessage(newMessage);

        // Notify
        notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }
    }

    // Case: Suggesting a company for a spam that doesn't exist in the DB - do nothing.
    // TODO: Prompt user to add this spam to the DB.
    else {
      Log.w(TAG, "Attempt to suggest a company for an SMS, but SMS wasn't found in the DB");
    }
  }

  // Undo's a company suggestion for a spam in the DB. If spam wasn't found in the DB - do nothing.
  public void undoCompanySuggestion(SmsMessage smsMessage, Company company) {

    // Fetch message from DB
    int index = searchDbMessage(smsMessage);

    // Message found:
    if (index != -1) {
      int companyIndex = searchCompany(company, dbMessages.get(index).getCompanyList());

      // Case: User is part of the suggesters of this company - Remove from suggesters list
      if (companyIndex != -1
          && dbMessages
              .get(index)
              .getCompanyList()
              .get(companyIndex)
              .getSuggestersList()
              .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {

        // Remove user from company's suggesters
        Company newCompany = dbMessages.get(index).getCompanyList().get(companyIndex);
        newCompany
            .toBuilder()
            .getSuggestersList()
            .remove(FirebaseAuthentication.getInstance().getCurrentUserId());

        // Update SmsMessage
        SmsMessage newMessage =
            dbMessages.get(index).toBuilder().setCompany(companyIndex, newCompany).build();

        // Push new SmsMessage to DB
        firestoreClient.modifyMessage(smsMessage, newMessage);
        modifyMessage(newMessage);

        // Notify
        notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }

      // Case: User is not part of the suggesters for this company - do nothing.
      else if (companyIndex != -1) {
        Log.w(
            TAG,
            "Attempt to undo a company suggestion for an SMS, but user isn't listed in the company's suggesters");
      }
    }

    // Message wasn't found in the DB
    else {
      Log.w(TAG, "Attempt to undo a company suggestion for an SMS, but SMS wasn't found in the DB");
    }
  }

  public void undoSuggestion(SmsMessage smsMessage) {
    // Check if user is part of the "suggesters" of this spam message
    if (smsMessage
        .getSuggestersList()
        .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {

      // Case: Other people had suggested this spam as well, update just the counter (-1)
      if (smsMessage.getSuggestersCount() > 1) {
        // 1. Create new suggesters list
        List<String> newSuggesters = new ArrayList<>(smsMessage.getSuggestersList());
        newSuggesters.remove(FirebaseAuthentication.getInstance().getCurrentUserId());
        // 2. Update the counter and build modified message
        SmsMessage newMessage =
            SmsMessage.newBuilder()
                .addAllSuggesters(newSuggesters)
                .setId(smsMessage.getId())
                .setReceivedAt(smsMessage.getReceivedAt())
                .setSender(smsMessage.getSender())
                .setBody(smsMessage.getBody())
                .setApproved(smsMessage.getApproved())
                .build();

        firestoreClient.modifyMessage(smsMessage, newMessage);
        modifyMessage(newMessage);

        // Notify
        notifyListeners(dbMessages.indexOf(newMessage), newMessage, Type.MODIFIED);
      }
      // Case: User is the only one suggested this spam
      else {
        removeMessage(smsMessage);
        notifyListeners(dbMessages.indexOf(smsMessage), smsMessage, Type.REMOVED);
      }
    }
  }

  // Search Db/Inbox messages by sender & body. Return -1 if no message found
  public static int searchMessage(SmsMessage smsMessage, List<SmsMessage> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getBody().contentEquals(smsMessage.getBody())
          && list.get(i).getSender().contentEquals(smsMessage.getSender())) {
        return i;
      }
    }
    return -1;
  }

  // Search company by: Name, Id, Address, Phone, Fax. Return -1 if none found.
  // Excludes fields that don't relate to the company's details (e.g SuggestersList)
  public int searchCompany(Company company, List<Company> list) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getName().contentEquals(company.getName())
          && list.get(i).getId().contentEquals(company.getId())
          && list.get(i).getAddress().contentEquals(company.getAddress())
          && list.get(i).getPhone().contentEquals(company.getPhone())
          && list.get(i).getFax().contentEquals(company.getFax())) {
        return i;
      }
    }

    return -1;
  }

  // Search message by sender & body. If no message found, return -1
  public int searchDbMessage(SmsMessage smsMessage) {
    return searchMessage(smsMessage, dbMessages);
  }

  // Search message by sender & body. If no message found, return -1
  public int searchInboxMessage(SmsMessage smsMessage) {
    return searchMessage(smsMessage, inboxMessages);
  }

  // Return -1 if no message found
  public int getDbIndexById(SmsMessage smsMessage) {
    for (int i = 0; i < dbMessages.size(); i++) {
      if (dbMessages.get(i).getId().contentEquals(smsMessage.getId())) {
        return i;
      }
    }
    return -1;
  }

  // Receive changes from the db (called from hasadna.noloan.common.FirestoreClient)
  public void updateChange(SmsMessage smsMessage, Type type) {
    switch (type) {
      case ADDED:
        addMessage(smsMessage);
        break;
      case MODIFIED:
        modifyMessage(smsMessage);
        break;
      case REMOVED:
        removeMessage(smsMessage);
        break;
    }
  }

  public void addMessage(SmsMessage smsMessage) {
    // If spam arrives from the db - Set the received date as of the date the user had received it
    // TODO: Spams in the DB have 1 field for received date, though they are suggested by multiple
    // users which might have received the same spam - at different dates. Think wheather or not
    // this is the right representaion of a spam in the DB - Perhaps have received date field in the
    // DB be as a list of received dates.
    if (searchInboxMessage(smsMessage) != -1) {
      smsMessage =
          smsMessage
              .toBuilder()
              .setReceivedAt(inboxMessages.get(searchInboxMessage(smsMessage)).getReceivedAt())
              .build();
    }
    dbMessages.add(smsMessage);

    notifyListeners(getDbIndexById(smsMessage), smsMessage, Type.ADDED);
  }

  public void removeMessage(SmsMessage smsMessage) {
    int index = getDbIndexById(smsMessage);
    if (index != -1) {
      dbMessages.remove(index);
      firestoreClient.deleteMessage(smsMessage);
      notifyListeners(index, smsMessage, Type.REMOVED);
    } else {
      Log.w(
          TAG,
          "Attempt to remove message by its ID, but message not found. This might come from inbox trying to find a similiar spam in the db.\nmessage id: "
              + smsMessage.getId());
    }
  }

  public void modifyMessage(SmsMessage newMessage) {
    int index = getDbIndexById(newMessage);
    if (index != -1) {
      dbMessages.set(index, newMessage);
      notifyListeners(index, newMessage, Type.MODIFIED);
    } else {
      Log.e(
          TAG,
          "Attempt to modify message but message not found.\n Message id: "
              + newMessage.getId()
              + "\nBody: "
              + newMessage.getBody()
              + "\nSender: "
              + newMessage.getSender());
    }
  }

  public void setMessagesListener(MessagesListener messagesListener) {
    this.messagesListeners.add(messagesListener);
  }

  public void notifyListeners(int index, SmsMessage smsMessage, Type type) {
    for (MessagesListener listener : messagesListeners) {
      switch (type) {
        case ADDED:
          listener.messageAdded(smsMessage);
          break;
        case MODIFIED:
          listener.messageModified(index);
          break;
        case REMOVED:
          listener.messageRemoved(index, smsMessage);
          break;
      }
    }
  }

  public List<SmsMessage> getDbMessages() {
    return dbMessages;
  }

  public List<SmsMessage> getInboxMessages() {
    return inboxMessages;
  }

  public void setInboxMessages(List<SmsMessage> inboxMessages) {
    this.inboxMessages = inboxMessages;
  }

  // Number of spam messages found in the user's inbox by body & sender
  public int countInboxSpam() {
    int count = 0;
    for (int i = 0; i < inboxMessages.size(); i++) {
      if (searchDbMessage(inboxMessages.get(i)) != -1) {
        count++;
      }
    }

    return count;
  }

  public interface MessagesListener {

    void messageAdded(SmsMessage newMessage);

    void messageModified(int index);

    // SmsMessage parameter is passed for cases the index already had been removed from the db list
    void messageRemoved(int index, SmsMessage smsMessage);
  }
}

