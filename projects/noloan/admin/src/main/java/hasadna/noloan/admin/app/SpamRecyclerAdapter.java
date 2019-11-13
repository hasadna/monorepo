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

public class SpamRecyclerAdapter
    extends RecyclerView.Adapter<SpamRecyclerAdapter.RecyclerViewHolder> {

  ArrayList<SmsMessage> messages;

  public SpamRecyclerAdapter() {

    messages = new ArrayList<>();
    SmsMessages dbMessages = SmsMessages.get();

    // maybe add getSuggestions to SmsMessages
    for (SmsMessage message : dbMessages.getDbMessages()) {
      if (message.getApproved()) {
        messages.add(message);
      }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    dbMessages.setMessagesListener(
        new SmsMessages.MessagesListener() {
          @Override
          public void messageAdded(SmsMessage smsMessage) {
            if (smsMessage.getApproved()) {
              messages.add(smsMessage);
            }
            handler.post(() -> notifyItemInserted(messages.size()));
          }

          @Override
          public void messageRemoved(int index, SmsMessage smsMessage) {
            if (smsMessage.getApproved()) {
              int i = SmsMessages.searchMessage(smsMessage, messages);
              messages.remove(smsMessage);
              handler.post(() -> notifyItemRemoved(i));
            }
          }

          @Override
          public void messageModified(int index) {
            SmsMessage smsMessage = dbMessages.getDbMessages().get(index);
            int i = SmsMessages.searchMessage(smsMessage, messages);
            if (i != -1 && smsMessage.getApproved()) { // message approved and in the list
              messages.set(i, smsMessage);
              handler.post(() -> notifyItemChanged(i));
            } else if (i != -1) // message not approved but in the list
            {
              messages.remove(i);
              handler.post(() -> notifyItemRemoved(i));
            } else // message approved but not in the list
            {
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
    return new RecyclerViewHolder(inflater.inflate(R.layout.spam_list_item, viewGroup, false));
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
    Button buttonDelete;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.spam_received_from);
      content = itemView.findViewById(R.id.spam_content);
      receivedAt = itemView.findViewById(R.id.spam_receivedAt);
      buttonDelete = itemView.findViewById(R.id.spam_button_delete);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());

      buttonDelete.setOnClickListener(
          view -> {
            FirestoreClient client = new FirestoreClient();
            client.deleteMessage(sms);
            Toast.makeText(view.getContext(), "deleted", Toast.LENGTH_SHORT).show();
          });
    }
  }
}

