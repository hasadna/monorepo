package hasadna.noloan.admin.app;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hasadna.noloan.common.FirestoreClient;
import hasadna.noloan.common.SmsMessages;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class SuggestionRecyclerAdapter
    extends RecyclerView.Adapter<SuggestionRecyclerAdapter.RecyclerViewHolder> {

  ArrayList<SmsMessage> messages;

  public SuggestionRecyclerAdapter() {
    messages = new ArrayList<>();
    SmsMessages dbMessages = SmsMessages.get();

    for (SmsMessage message : dbMessages.getDbMessages()) {
      if (!message.getApproved()) {
        this.messages.add(message);
      }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    // Listening to changes in the DB
    dbMessages.setMessagesListener(
        new SmsMessages.MessagesListener() {
          @Override
          public void messageAdded(SmsMessage smsMessage) {
            if (!smsMessage.getApproved()) {
              messages.add(smsMessage);
              handler.post(() -> notifyItemInserted(messages.size()));
            }
          }

          @Override
          public void messageRemoved(int index, SmsMessage smsMessage) {
            if (!smsMessage.getApproved()) {
              int i = SmsMessages.searchMessage(smsMessage, messages);
              messages.remove(i);
              handler.post(() -> notifyItemRemoved(i));
            }
          }

          @Override
          public void messageModified(int index) {
            SmsMessage smsMessage = dbMessages.getDbMessages().get(index);
            int i = SmsMessages.searchMessage(smsMessage, messages);

            if (i != -1 && !smsMessage.getApproved()) { // Message not in the list and not approve
              messages.set(i, smsMessage);
              handler.post(() -> notifyItemChanged(i));
            } else if (i
                != -1) { // Message not in the list and approved, need to be removed from the list
              messages.remove(i);
              handler.post(() -> notifyItemRemoved(i));
            } else { // Message not in the list and not approve
              messages.add(smsMessage);
              handler.post(() -> notifyItemInserted(messages.size()));
            }
          }
        });
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(
        inflater.inflate(R.layout.suggestion_list_item, viewGroup, false));
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
    Button buttonAccept, buttonDelete;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.suggetions_received_from);
      content = itemView.findViewById(R.id.suggetions_content);
      receivedAt = itemView.findViewById(R.id.suggetions_receivedAt);
      buttonAccept = itemView.findViewById(R.id.suggetions_button_accept);
      buttonDelete = itemView.findViewById(R.id.suggetions_button_delete);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());
      buttonAccept.setOnClickListener(
          view -> {
            FirestoreClient client = new FirestoreClient();
            SmsMessage approved = sms.toBuilder().setApproved(true).build();
            client.modifyMessage(sms, approved);
            Toast.makeText(view.getContext(), "accepted", Toast.LENGTH_SHORT).show();
          });
      buttonDelete.setOnClickListener(
          view -> {
            FirestoreClient client = new FirestoreClient();
            client.deleteMessage(sms);
            Toast.makeText(view.getContext(), "deleted", Toast.LENGTH_SHORT).show();
          });
    }
  }
}

