package hasadna.noloan.admin.app;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hasadna.noloan.admin.app.firestore.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;

public class RecyclerAdapter
    extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

  SpamHolder spam;

  public RecyclerAdapter() {
    spam = SpamHolder.getInstance();
    Handler handler = new Handler(Looper.getMainLooper());

    spam.setSpamListener(
        new SpamHolder.SpamListener() {
          @Override
          public void spamAdded() {
            handler.post(() -> notifyItemInserted(spam.getSpam().size()));
          }

          @Override
          public void spamRemoved() {
            handler.post(() -> notifyItemRemoved(spam.getSpam().size()));
          }

          @Override
          public void spamModified(int index) {
            handler.post(() -> notifyItemChanged(index));
          }
        });
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    return new RecyclerViewHolder(inflater.inflate(R.layout.messages_list_item, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
    recyclerViewHolder.bind(spam.getSpam().get(i));
  }

  @Override
  public int getItemCount() {
    return spam.getSpam().size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView from, content, receivedAt;
    Button buttonAccept;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonAccept = itemView.findViewById(R.id.button_accept);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());
      buttonAccept.setOnClickListener(view -> {
        FirestoreClient client = new FirestoreClient();
        client.writeMessage(sms, FirestoreClient.SPAM_COLLECTION_PATH);
        Toast.makeText(view.getContext(), "accepted", Toast.LENGTH_SHORT).show();
      });
    }
  }
}

