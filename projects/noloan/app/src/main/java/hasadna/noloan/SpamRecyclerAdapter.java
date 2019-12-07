package hasadna.noloan;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import hasadna.noloan.common.FirebaseAuthentication;
import hasadna.noloan.common.SmsMessages;
import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class SpamRecyclerAdapter
    extends RecyclerView.Adapter<SpamRecyclerAdapter.RecyclerViewHolder> {
  private static final String TAG = "SpamRecyclerAdapter";

  List<SmsMessage> inboxSpam;

  public SpamRecyclerAdapter() {
    Handler handler = new Handler(Looper.myLooper());
    List<SmsMessage> dbMessages = SmsMessages.get().getDbMessages();
    List<SmsMessage> inboxMessages = SmsMessages.get().getInboxMessages();

    inboxSpam = new ArrayList<>();

    // Fetch spams from DB that are in user's inbox
    for (SmsMessage sms : inboxMessages) {
      int dbIndex = SmsMessages.searchMessage(sms, dbMessages);
      if (dbIndex != -1) {
        // Add message from the DB - keep received date of user's message
        inboxSpam.add(
            dbMessages.get(dbIndex).toBuilder().setReceivedAt(sms.getReceivedAt()).build());
      }
    }

    // Listen to db smsMessages. Update just relevant messages that are in the inbox as well.
    // Keep the received date of the user's message.
    SmsMessages.get()
        .setMessagesListener(
            new SmsMessages.MessagesListener() {
              @Override
              public void messageAdded(SmsMessage newMessage) {
                int inboxIndex = SmsMessages.get().searchMessage(newMessage, inboxMessages);
                if (inboxIndex != -1) {
                  // Add message - keep received date of user's message
                  inboxSpam.add(
                      newMessage
                          .toBuilder()
                          .setReceivedAt(inboxMessages.get(inboxIndex).getReceivedAt())
                          .build());
                  handler.post(() -> notifyItemInserted(inboxSpam.size()));
                }
              }

              @Override
              public void messageModified(int index) {
                int inboxSpamIndex =
                    SmsMessages.get().searchMessage(dbMessages.get(index), inboxSpam);
                if (inboxSpamIndex != -1) {
                  // Modify message - keep received date of user's message
                  inboxSpam.set(
                      inboxSpamIndex,
                      dbMessages
                          .get(index)
                          .toBuilder()
                          .setReceivedAt(inboxMessages.get(inboxSpamIndex).getReceivedAt())
                          .build());
                  handler.post(() -> notifyItemChanged(inboxSpamIndex));
                }
              }

              @Override
              public void messageRemoved(int index, SmsMessage smsMessage) {
                int inboxSpamIndex = SmsMessages.get().searchMessage(smsMessage, inboxSpam);
                if (inboxSpamIndex != -1) {
                  inboxSpam.remove(inboxSpamIndex);
                  handler.post(() -> notifyItemRemoved(inboxSpamIndex));
                }
              }
            });
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(inflater.inflate(R.layout.spam_list_item, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
    recyclerViewHolder.bind(inboxSpam.get(i));
  }

  @Override
  public int getItemCount() {
    return inboxSpam.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView from, content, receivedAt, counter;
    Button buttonCreateLawsuit;
    Button buttonUndoSuggestion;
    Button buttonAddSuggestion;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonCreateLawsuit = itemView.findViewById(R.id.Button_createLawsuit);
      buttonUndoSuggestion = itemView.findViewById(R.id.Button_undoSuggestion);
      buttonAddSuggestion = itemView.findViewById(R.id.Button_addSuggestion);
      counter = itemView.findViewById(R.id.textView_suggestCounter);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());

      // Set received date to Hebrew format
      try {
        Calendar calendar = Calendar.getInstance();
        Locale local = new Locale("he");
        calendar.setTime(new SimpleDateFormat("dd/M/yyyy", local).parse(sms.getReceivedAt()));
        receivedAt.setText(
            String.format(
                itemView.getContext().getString(R.string.list_item_date),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, local),
                calendar.get(Calendar.YEAR)));
      } catch (ParseException e) {
        Log.e(TAG, "Error parsing sms.ReceivedAt() to Date object\n" + e.getMessage());
      }

      counter.setText(
          itemView
              .getResources()
              .getString(R.string.list_item_textView_spam_counter, sms.getSuggestersCount()));

      // Click on a message, from there (with message's details) move to the lawsuitPdfActivity
      // TODO: See which more fields in the lawsuit form can be understood from the SMS / other
      // DATA.
      buttonCreateLawsuit.setOnClickListener(
          view -> {
            Intent intentToLawsuitForm = new Intent(view.getContext(), LawsuitActivity.class);
            // TODO: Check if preferably to pass the SmsMessage.Proto object itself, rather than its
            // fields.
            intentToLawsuitForm.putExtra("receivedAt", sms.getReceivedAt());
            intentToLawsuitForm.putExtra("from", sms.getSender());
            intentToLawsuitForm.putExtra("body", sms.getBody());
            intentToLawsuitForm.putExtra("id", sms.getId());

            view.getContext().startActivity(intentToLawsuitForm);
          });

      // If user is had suggested this spam - Toggle "Undo suggestion" / Add suggestion options.
      toggleUndoButton(sms);

      buttonUndoSuggestion.setOnClickListener(
          v -> {
            SmsMessages.get().undoSuggestion(sms);
          });

      buttonAddSuggestion.setOnClickListener(
          view -> {
            SmsMessages.get().suggestMessage(sms);
          });
    }

    // Displays the "Undo suggestion" button, in case user had suggested this message.
    public void toggleUndoButton(SmsMessage smsMessage) {
      if (smsMessage
          .getSuggestersList()
          .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
        buttonAddSuggestion.setVisibility(View.INVISIBLE);
        buttonUndoSuggestion.setVisibility((View.VISIBLE));
      } else {
        buttonAddSuggestion.setVisibility(View.VISIBLE);
        buttonUndoSuggestion.setVisibility((View.INVISIBLE));
      }
    }
  }
}

