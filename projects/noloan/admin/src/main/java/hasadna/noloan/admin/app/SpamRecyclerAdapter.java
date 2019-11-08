package hasadna.noloan.admin.app;

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
import android.widget.Toast;

import java.util.ArrayList;

import hasadna.noloan.firebase.DbMessages;
import hasadna.noloan.firebase.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class SpamRecyclerAdapter
    extends RecyclerView.Adapter<SpamRecyclerAdapter.RecyclerViewHolder> {

  ArrayList<SmsMessage> messages;

  public SpamRecyclerAdapter() {

    DbMessages dbMessages = DbMessages.getInstance();
    messages = new ArrayList<>();

    for (SmsMessage message : dbMessages.getMessages()) {
      if (message.getApproved()) {
        this.messages.add(message);
      }
    }
    Handler handler = new Handler(Looper.getMainLooper());

    dbMessages.addMessagesListener(new DbMessages.MessagesListener() {
      @Override
      public void messageAdded(SmsMessage smsMessage) {
        if (smsMessage.getApproved()) {
          messages.add(smsMessage);
          handler.post(() -> notifyItemInserted(messages.size()));
        }
      }

      @Override
      public void messageModified(int index) {
        SmsMessage smsMessage = dbMessages.getMessages().get(index);
        if (smsMessage.getApproved()) {
          int i = messages.indexOf(smsMessage);
          if (i != -1) {
            messages.set(i, smsMessage);
            handler.post(() -> notifyItemChanged(i));
          }
          messages.add(smsMessage);
          handler.post(() -> notifyItemInserted(messages.size()));
        }
      }

      @Override
      public void messageRemoved(int index, SmsMessage smsMessage) {
        if (smsMessage.getApproved()) {
          int i = messages.indexOf(smsMessage);
          if (i != -1) {
            messages.remove(smsMessage);
            handler.post(() -> notifyItemRemoved(i));
          }
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

