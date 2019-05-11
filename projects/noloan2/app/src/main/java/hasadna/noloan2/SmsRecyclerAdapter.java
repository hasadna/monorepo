package hasadna.noloan2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan2.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class SmsRecyclerAdapter
    extends RecyclerView.Adapter<SmsRecyclerAdapter.RecyclerViewHolder> {

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
    return new RecyclerViewHolder(inflater.inflate(R.layout.messages_list_item, viewGroup, false));
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
    Button buttonSmsToLawsuit;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonSmsToLawsuit = itemView.findViewById(R.id.button_smsToLawsuit);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());
      // Click on a spam message, from there (with message's details) move to the lawsuitPdfActivity
      // TODO: See which more fields in the lawsuit form can be understood from the SMS / other
      // DATA.
      buttonSmsToLawsuit.setOnClickListener(
          view -> {
            Intent intentToLawsuitForm = new Intent(view.getContext(), LawsuitActivity.class);
            intentToLawsuitForm.putExtra("receivedAt", sms.getReceivedAt());
            intentToLawsuitForm.putExtra("from", sms.getSender());
            intentToLawsuitForm.putExtra("body", sms.getBody());
            view.getContext().startActivity(intentToLawsuitForm);
          });
    }
  }
}

