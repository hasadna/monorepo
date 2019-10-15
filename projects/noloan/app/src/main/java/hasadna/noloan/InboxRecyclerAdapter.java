package hasadna.noloan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class InboxRecyclerAdapter
    extends RecyclerView.Adapter<InboxRecyclerAdapter.RecyclerViewHolder> {
  private static final String TAG = "InboxRecyclerAdapter";
  List<SmsMessage> messages;
  RecyclerViewHolder recyclerViewHolder;

  public InboxRecyclerAdapter(List<SmsMessage> messages) {
    if (messages.size() == 0) {
      this.messages = new ArrayList<>();
      SmsMessage noMessage = SmsMessage.newBuilder().setSender("אין הודעות").build();
      this.messages.add(noMessage);
    } else {
      this.messages = messages;
    }
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(inflater.inflate(R.layout.inbox_list_item, viewGroup, false));
  }

  // Search message by Body and Sender. Return -1 of no message found
  public int searchMessage(SmsMessage smsMessage) {
    for (int i = 0; i < messages.size(); i++) {
      if (messages.get(i).getBody().contentEquals(smsMessage.getBody())
          && messages.get(i).getSender().contentEquals(smsMessage.getSender())) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
    recyclerViewHolder.bind(messages.get(i));
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView from, content, receivedAt, counter;
    Button buttonCreateLawsuit;
    Button buttonAddSuggestion;
    Button buttonUndoSuggestion;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      counter = itemView.findViewById(R.id.textView_suggestCounter);
      buttonCreateLawsuit = itemView.findViewById(R.id.Button_createLawsuit);
      buttonAddSuggestion = itemView.findViewById(R.id.Button_addSuggestion);
      buttonUndoSuggestion = itemView.findViewById(R.id.Button_undoSuggestionT);
    }

    public void bind(SmsMessage sms) {
      from.setText(
          String.format(itemView.getContext().getString(R.string.list_item_from), sms.getSender()));
      content.setText(sms.getBody());

      // Search if message was suggested / user suggested: update counter / add undo button
      if (DbMessages.getInstance().searchMessage(sms) != -1) {
        counter.setText(
            itemView
                .getResources()
                .getString(
                    R.string.list_item_textView_spam_counter,
                    DbMessages.getInstance()
                        .getMessages()
                        .get(DbMessages.getInstance().searchMessage(sms))
                        .getCounter()));

        // Toggle undo button
        if (DbMessages.getInstance()
            .getMessages()
            .get(DbMessages.getInstance().searchMessage((sms)))
            .getSuggestersList()
            .contains(DbMessages.getInstance().getFirebaseUser().getUid())) {
          buttonAddSuggestion.setVisibility(View.INVISIBLE);
          buttonUndoSuggestion.setVisibility((View.VISIBLE));
          buttonUndoSuggestion.setOnClickListener(
              view ->
                  DbMessages.getInstance()
                      .undoSuggestion(
                          DbMessages.getInstance()
                              .getMessages()
                              .get(DbMessages.getInstance().searchMessage((sms)))));
        } else {
          buttonAddSuggestion.setVisibility(View.VISIBLE);
          buttonUndoSuggestion.setVisibility((View.INVISIBLE));
        }
      }
      // Case: No one suggested this message
      else {
        counter.setText(
            itemView.getResources().getString(R.string.list_item_textView_spam_counter, 0));
        buttonAddSuggestion.setVisibility(View.VISIBLE);
        buttonUndoSuggestion.setVisibility((View.INVISIBLE));
      }

      // Localize Hebrew date format
      Calendar calendar = Calendar.getInstance();
      Date receivedDate;
      Locale local = new Locale("he");
      try {
        receivedDate = new SimpleDateFormat("dd/M/yyyy", local).parse(sms.getReceivedAt());
        calendar.setTime(receivedDate);
        receivedAt.setText(
            String.format(
                itemView.getContext().getString(R.string.list_item_date),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, local),
                calendar.get(Calendar.YEAR)));
      } catch (ParseException e) {
        Log.e(TAG, "Error parsing sms.ReceivedAt() to Date object\n" + e.getMessage());
      }

      // Click on a message, from there (with message's details) move to the lawsuitPdfActivity
      // TODO: See which more fields in the lawsuit form can be understood from the SMS / other
      // DATA.
      buttonCreateLawsuit.setOnClickListener(
          view -> {
            Intent intentToLawsuitForm = new Intent(view.getContext(), LawsuitActivity.class);
            intentToLawsuitForm.putExtra("receivedAt", sms.getReceivedAt());
            intentToLawsuitForm.putExtra("from", sms.getSender());
            intentToLawsuitForm.putExtra("body", sms.getBody());
            view.getContext().startActivity(intentToLawsuitForm);
          });

      buttonAddSuggestion.setOnClickListener(view -> DbMessages.getInstance().suggestMessage(sms));
    }
  }
}

