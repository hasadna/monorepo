package hasadna.noloan;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class SpamRecyclerAdapter
    extends RecyclerView.Adapter<SpamRecyclerAdapter.RecyclerViewHolder> {

  DbMessages dbMessages;

  public SpamRecyclerAdapter() {
    dbMessages = DbMessages.getInstance();
    Handler handler = new Handler(Looper.myLooper());

    // Listen to db messages
    dbMessages.setMessagesListener(
        new DbMessages.MessagesListener() {
          @Override
          public void messageAdded(SmsMessage newMessage) {
            handler.post(() -> notifyItemInserted(dbMessages.getMessages().size()));
          }

          @Override
          public void messageModified(int index) {
            handler.post(() -> notifyItemChanged(index));
          }

          @Override
          public void messageRemoved(int index, SmsMessage smsMessage) {
            handler.post(() -> notifyItemRemoved(index));
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
    recyclerViewHolder.bind(dbMessages.getMessages().get(i));
  }

  @Override
  public int getItemCount() {
    return dbMessages.getMessages().size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView from, content, receivedAt, counter;
    Button buttonCreateLawsuit;
    Button buttonUndoSuggestion;
    Button buttonAddSuggestion;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonCreateLawsuit = itemView.findViewById(R.id.Button_createLawsuit);
      buttonUndoSuggestion = itemView.findViewById(R.id.Button_undoSuggestion);
      buttonAddSuggestion = itemView.findViewById(R.id.Button_addSuggestion);
      counter = itemView.findViewById(R.id.textView_suggestCounter);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());
      counter.setText(
          itemView
              .getResources()
              .getString(R.string.list_item_textView_spam_counter, sms.getSuggestersCount()));

      // Click on a message, from there (with message's details) move to the lawsuitPdfActivity
      // TODO: See which more fields in the lawsuit form can be understood from the SMS / other
      // DATA.
      buttonCreateLawsuit.setOnClickListener(
          view -> {
            Intent intentToLawsuitForm = new Intent(view.getContext(), LawsuitActivity.class);
            intentToLawsuitForm.putExtra("receivedAt", sms.getReceivedAt());
            intentToLawsuitForm.putExtra("from", sms.getSender());
            intentToLawsuitForm.putExtra("body", sms.getBody());
            view.getContext().startActivity(intentToLawsuitForm);
          });

      // If user is had suggested this spam - Toggle "Undo suggestion" / Add suggestion options.
      toggleUndoButton(sms);

      buttonUndoSuggestion.setOnClickListener(
          v -> {
            dbMessages.undoSuggestion(sms);
          });

      buttonAddSuggestion.setOnClickListener(
          view -> {
            DbMessages.getInstance().suggestMessage(sms);
          });
    }

    // Displays the "Undo suggestion" button, in case user had suggested this message.
    public void toggleUndoButton(SmsMessage smsMessage) {
      if (smsMessage.getSuggestersList().contains(DbMessages.getInstance().firebaseUser.getUid())) {
        buttonAddSuggestion.setVisibility(View.INVISIBLE);
        buttonUndoSuggestion.setVisibility((View.VISIBLE));
      } else {
        buttonAddSuggestion.setVisibility(View.VISIBLE);
        buttonUndoSuggestion.setVisibility((View.INVISIBLE));
      }
    }
  }
}

