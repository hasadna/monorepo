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

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class SuggestionRecyclerAdapter
    extends RecyclerView.Adapter<SuggestionRecyclerAdapter.RecyclerViewHolder> {

  DbMessages suggestions;

  public SuggestionRecyclerAdapter() {
    suggestions = DbMessages.getInstance();
    Handler handler = new Handler(Looper.myLooper());

    suggestions.setSuggestionsListener(
        new DbMessages.MessagesListener() {
          @Override
          public void messageAdded() {
            handler.post(() -> notifyItemInserted(suggestions.getSuggestions().size()));
          }

          @Override
          public void messageRemoved() {
            handler.post(() -> notifyItemRemoved(suggestions.getSuggestions().size()));
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
    return new RecyclerViewHolder(
        inflater.inflate(R.layout.suggestion_list_item, viewGroup, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
    recyclerViewHolder.bind(suggestions.getSuggestions().get(i));
  }

  @Override
  public int getItemCount() {
    return suggestions.getSuggestions().size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView from, content, receivedAt;
    Button buttonCreateLawsuit;
    Button buttonRemoveSuggestion;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);
      from = itemView.findViewById(R.id.received_from);
      content = itemView.findViewById(R.id.content);
      receivedAt = itemView.findViewById(R.id.receivedAt);
      buttonCreateLawsuit = itemView.findViewById(R.id.Button_createLawsuit);
      buttonRemoveSuggestion = itemView.findViewById(R.id.Button_removeSuggestion);
    }

    public void bind(SmsMessage sms) {
      from.setText(sms.getSender());
      content.setText(sms.getBody());
      receivedAt.setText(sms.getReceivedAt());
      // Click on a message, from there (with message's details) move to the
      // lawsuitPdfActivity
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

      buttonRemoveSuggestion.setOnClickListener(
          v -> {
            // TODO: Add a counter to the suggested spam and apply the remove only if there's 1
            // suggestion
            FirestoreClient client = new FirestoreClient();
            client.deleteMessage(sms, FirestoreClient.USER_SUGGEST_COLLECTION);
          });
    }
  }
}

