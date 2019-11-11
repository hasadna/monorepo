package hasadna.noloan;

import android.content.Intent;

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
import java.util.Locale;

import hasadna.noloan.common.DbMessages;
import hasadna.noloan.common.FirebaseAuthentication;
import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class InboxRecyclerAdapter
    extends RecyclerView.Adapter<InboxRecyclerAdapter.RecyclerViewHolder> {
  private static final String TAG = "InboxRecyclerAdapter";

  private ArrayList<SmsMessage> messages;

  public InboxRecyclerAdapter(ArrayList<SmsMessage> inbox) {
    messages = inbox;
    if (messages.size() == 0) {
      SmsMessage noMessage = SmsMessage.newBuilder().setSender("אין הודעות").build();
      messages.add(noMessage);
    }
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(inflater.inflate(R.layout.inbox_list_item, viewGroup, false));
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
      DbMessages dbMessages = DbMessages.getInstance();
      int index = DbMessages.findSms(sms, dbMessages.getMessages());
      if (index != -1) {
        SmsMessage message = dbMessages.getMessages().get(index);
        counter.setText(itemView.getResources().getString(R.string.list_item_textView_spam_counter, message.getSuggestersCount()));

        // Toggle undo button
        if (message.getSuggestersList().contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
          buttonAddSuggestion.setVisibility(View.INVISIBLE);
          buttonUndoSuggestion.setVisibility((View.VISIBLE));
          buttonUndoSuggestion.setOnClickListener(view -> dbMessages.removeMessage(message));
        } else {
          buttonAddSuggestion.setVisibility(View.VISIBLE);
          buttonUndoSuggestion.setVisibility((View.INVISIBLE));
        }
      }
      // Case: No one suggested this message
      else {
        counter.setText(itemView.getResources().getString(R.string.list_item_textView_spam_counter, 0));
        buttonAddSuggestion.setVisibility(View.VISIBLE);
        buttonUndoSuggestion.setVisibility((View.INVISIBLE));
      }

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

      buttonAddSuggestion.setOnClickListener(view -> DbMessages.getInstance().addMessage(sms));
    }
  }
}

