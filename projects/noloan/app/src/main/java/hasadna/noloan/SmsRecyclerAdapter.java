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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class SmsRecyclerAdapter
    extends RecyclerView.Adapter<SmsRecyclerAdapter.RecyclerViewHolder> {
  private static final String TAG = "SmsRecyclerAdapter";
  List<SmsMessage> messages;

  public SmsRecyclerAdapter(List<SmsMessage> messages) {
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
    return new RecyclerViewHolder(inflater.inflate(R.layout.sms_list_item, viewGroup, false));
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

    TextView from, content, receivedAt;
    Button buttonCreateSmsLawsuit;
    Button buttonSuggestSpam;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonCreateSmsLawsuit = itemView.findViewById(R.id.Button_createLawsuit);
      buttonSuggestSpam = itemView.findViewById(R.id.Button_removeSuggestion);
    }

    public void bind(SmsMessage sms) {
      from.setText(
          String.format(itemView.getContext().getString(R.string.list_item_from), sms.getSender()));
      content.setText(sms.getBody());
      Calendar calendar = Calendar.getInstance();
      Date receivedDate;

      // Localize Hebrew date format
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
      buttonCreateSmsLawsuit.setOnClickListener(
          view -> {
            Intent intentToLawsuitForm = new Intent(view.getContext(), LawsuitActivity.class);
            intentToLawsuitForm.putExtra("receivedAt", sms.getReceivedAt());
            intentToLawsuitForm.putExtra("from", sms.getSender());
            intentToLawsuitForm.putExtra("body", sms.getBody());
            view.getContext().startActivity(intentToLawsuitForm);
          });

      buttonSuggestSpam.setOnClickListener(
          view -> {
            FirestoreClient client = new FirestoreClient();
            client.writeMessage(sms, FirestoreClient.USER_SUGGEST_COLLECTION);
            Toast.makeText(view.getContext(), "suggested", Toast.LENGTH_SHORT).show();
          });
    }
  }
}

