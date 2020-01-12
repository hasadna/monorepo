package hasadna.noloan;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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

public class InboxRecyclerAdapter
    extends RecyclerView.Adapter<InboxRecyclerAdapter.RecyclerViewHolder> implements Filterable {
  private static final String TAG = "InboxRecyclerAdapter";

  // List to operate on, for search queries (Copy of SmsMessages.getInboxMessages)
  private List<SmsMessage> filteredMessagesList;

  public InboxRecyclerAdapter() {

    // Case: No messages in inbox - create a dummy SmsMessage object
    // TODO: Think of a different approach - Perhaps display the recycler empty background with text
    // "אין הודעות בתיבה" e.g
    if (SmsMessages.get().getInboxMessages().size() == 0) {
      SmsMessage noMessage = SmsMessage.newBuilder().setSender("אין הודעות").build();
      SmsMessages.get().getInboxMessages().add(noMessage);
    }

    // Copy all inbox messages to the dedicated filtered list
    this.filteredMessagesList = new ArrayList<>(SmsMessages.get().getInboxMessages());
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(inflater.inflate(R.layout.inbox_list_item, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
    recyclerViewHolder.bind(filteredMessagesList.get(i));
  }

  @Override
  public int getItemCount() {
    return filteredMessagesList.size();
  }

  @Override
  public Filter getFilter() {
    return SmsMessagesFilter;
  }

  // Search Filter for SmsMessages
  private Filter SmsMessagesFilter =
      new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
          List<SmsMessage> filteredList = new ArrayList<>();

          // If query's empty - display all messages
          if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(SmsMessages.get().getInboxMessages());
          }

          // Query not empty - Search pattern in messages list (Body/Sender/Date received)
          else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            // Search Body / Sender / Received date
            for (SmsMessage sms : SmsMessages.get().getInboxMessages()) {
              if (sms.getBody().toLowerCase().contains(filterPattern)
                  || sms.getSender().toLowerCase().contains(filterPattern)
                  || sms.getReceivedAt().toLowerCase().contains(filterPattern)) {

                // Resulted list
                filteredList.add(sms);
              }
            }
          }

          // Build resulted list, calls 'publishResults' to publish the list to RecyclerViewer
          FilterResults results = new FilterResults();
          results.values = filteredList;
          return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          // Clears last result
          filteredMessagesList.clear();
          // Update new results
          filteredMessagesList.addAll((List) results.values);
          notifyDataSetChanged();
        }
      };

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
      if (SmsMessages.get().searchDbMessage(sms) != -1) {
        counter.setText(
            itemView
                .getResources()
                .getString(
                    R.string.list_item_textView_spam_counter,
                    SmsMessages.get()
                        .getDbMessages()
                        .get(SmsMessages.get().searchDbMessage(sms))
                        .getSuggestersCount()));

        // Toggle undo button
        if (SmsMessages.get()
            .getDbMessages()
            .get(SmsMessages.get().searchDbMessage(sms))
            .getSuggestersList()
            .contains(FirebaseAuthentication.getInstance().getCurrentUserId())) {
          buttonAddSuggestion.setVisibility(View.INVISIBLE);
          buttonUndoSuggestion.setVisibility((View.VISIBLE));
          buttonUndoSuggestion.setOnClickListener(
              view ->
                  SmsMessages.get()
                      .undoSuggestion(
                          SmsMessages.get()
                              .getDbMessages()
                              .get(SmsMessages.get().searchDbMessage((sms)))));
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

      buttonAddSuggestion.setOnClickListener(view -> SmsMessages.get().suggestMessage(sms));
    }
  }
}

