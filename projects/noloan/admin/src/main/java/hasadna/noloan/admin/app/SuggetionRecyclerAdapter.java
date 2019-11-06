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

public class SuggetionRecyclerAdapter
    extends RecyclerView.Adapter<SuggetionRecyclerAdapter.RecyclerViewHolder> {

  DbMessages dbMessages;

  ArrayList<SmsMessage> messages;

  public SuggetionRecyclerAdapter() {
    dbMessages = dbMessages.getInstance();
    messages = new ArrayList<>();

    ArrayList<SmsMessage> list = dbMessages.getMessages();
    for (int i = 0; i < list.size(); i++) {
      SmsMessage message = list.get(i);
      if (!message.getApproved()) {
        Log.d("!!!!!!!!!!!!!!!!!!", "bla");
        this.messages.add(message);
      }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    dbMessages.addMessagesListener(
        new DbMessages.MessagesListener() {
          @Override
          public void messageAdded(SmsMessage smsMessage) {
            if (!smsMessage.getApproved()) {
              messages.add(smsMessage);
              handler.post(() -> notifyItemInserted(messages.size()));
            }
          }

          @Override
          public void messageModified(int index) {
            SmsMessage smsMessage = dbMessages.getMessages().get(index);
            if (!smsMessage.getApproved()) {
              int i = messages.indexOf(smsMessage);
              // already in the list
              if (i != -1) {
                messages.remove(i);
                messages.add(i, smsMessage);
                handler.post(() -> notifyItemChanged(i));
              } else // new approved
              {
                messages.add(smsMessage);
                handler.post(() -> notifyItemInserted(messages.size()));
              }
            }
            else {
              int i;
              for(i = 0; i< messages.size();i++)
              {
                if(messages.get(i).getId().equals(smsMessage.getId()))
                {
                  break;
                }
              }
              Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!",i+"");
              if (i != -1)
              {
                messages.remove(i);
                int finalI = i; // to use i in the lambda it need to be final.
                handler.post(() -> notifyItemChanged(finalI));
              }
            }
          }

          @Override
          public void messageRemoved(int index, SmsMessage smsMessage) {
            if (!smsMessage.getApproved()) {
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

            SmsMessage aprroved = sms.toBuilder().setApproved(true).build();
            Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", aprroved.getApproved() + "");
            client.modifyMessage(sms, aprroved);

            //client.writeMessage(sms);
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

