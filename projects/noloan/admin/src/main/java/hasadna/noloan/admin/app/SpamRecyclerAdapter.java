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

import hasadna.noloan.common.DbMessages;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class SpamRecyclerAdapter
    extends RecyclerView.Adapter<SpamRecyclerAdapter.RecyclerViewHolder> {


  public SpamRecyclerAdapter() {

    DbMessages dbMessages = hasadna.noloan.common.DbMessages.getInstance();
    Handler handler = new Handler(Looper.getMainLooper());

    dbMessages.addMessagesListener(
        new hasadna.noloan.common.DbMessages.MessagesListener() {
          @Override
          public void messageAdded(SmsMessage smsMessage) {
            handler.post(() -> notifyItemInserted(dbMessages.getMessages().size()));
          }

          @Override
          public void messageRemoved(int index, SmsMessage smsMessage) {
            handler.post(() -> notifyItemRemoved(index));
          }

          @Override
          public void messageModified(int index) {
            handler.post(() -> notifyItemChanged(index));
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
    recyclerViewHolder.bind(DbMessages.getInstance().getMessages().get(i));
  }

  @Override
  public int getItemCount() {
    return DbMessages.getInstance().getMessages().size();
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
            DbMessages.getInstance().removeMessage(sms);
            Toast.makeText(view.getContext(), "deleted", Toast.LENGTH_SHORT).show();
          });
    }
  }
}

