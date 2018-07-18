package projects.noloan.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import projects.noloan.app.Protos.SmsMessage;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.net.Uri;

/* Adapter for SMS messages */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final String TAG = "MessagesAdapter";

  private List<SmsMessage> messages = new ArrayList<>();
  private Context context;

  public interface OnDataFetched {
    void onFetched();
  }

  public class SummaryViewHolder extends RecyclerView.ViewHolder {
    TextView textView;

    SummaryViewHolder(View itemView) {
      super(itemView);
      textView = itemView.findViewById(R.id.status_summary);

      TextView lawsuitTextView = itemView.findViewById(R.id.status_lawsuit);
      Spanned spanned =
          Html.fromHtml(
              context.getResources().getString(R.string.text_lawsuit), Html.FROM_HTML_MODE_COMPACT);
      lawsuitTextView.setMovementMethod(LinkMovementMethod.getInstance());
      lawsuitTextView.setText(spanned);
    }
  }

  public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView fromText;
    TextView receivedAtText;
    TextView contentText;

    ItemViewHolder(View itemView) {
      super(itemView);
      fromText = itemView.findViewById(R.id.received_from);
      receivedAtText = itemView.findViewById(R.id.received_at);
      contentText = itemView.findViewById(R.id.content);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Log.d(TAG, "Item #" + getAdapterPosition() + " pressed.");
    }
  }

  public MessagesAdapter(Context context) {
    this.context = context;
    this.messages = new ArrayList<>(readMessages());
  }

  // Create new views (invoked by the layout manager)
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    switch (viewType) {
      case 0:
        View summaryView =
            LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_summary_item, viewGroup, false);
        return new SummaryViewHolder(summaryView);
      case 1:
      default:
        View view =
            LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_list_item, viewGroup, false);
        return new ItemViewHolder(view);
    }
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    switch (holder.getItemViewType()) {
      case 0: // summary
        int messagesCount = messages.size();
        SummaryViewHolder viewHolder = (SummaryViewHolder) holder;
        viewHolder.textView.setText(
            Html.fromHtml(
                context
                    .getResources()
                    .getString(R.string.content_summary, String.valueOf(messagesCount))));
        break;
      case 1:
        SmsMessage message = messages.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.fromText.setText(
            context.getResources().getString(R.string.list_item_title, message.getSender()));
        itemViewHolder.receivedAtText.setText(
            context
                .getResources()
                .getString(
                    R.string.list_item_date, Util.formatTime(message.getReceivedDateTime())));
        itemViewHolder.contentText.setText(message.getContents());
        break;
    }
  }

  @Override
  public int getItemCount() {
    return messages.size();
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }

  public void deAttachContext() {
    context = null;
  }

  private List<SmsMessage> readMessages() {
    Log.d(TAG, "readMessages");
    List<SmsMessage> messages = new ArrayList<>();

    Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(mSmsQueryUri, null, null, null, null);
      if (cursor == null) {
        Log.i(TAG, "cursor is null. uri: " + mSmsQueryUri);
      }
      for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {

        SmsMessage message = SmsMessage.getDefaultInstance();
        SmsMessage.Builder messageBuilder = message.toBuilder();

        final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        final String receivedTime = cursor.getString(cursor.getColumnIndexOrThrow("date_sent"));
        final String senderPhone = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        messageBuilder.setContents(body);
        messageBuilder.setSender(senderPhone);
        messageBuilder.setReceivedDateTime(Long.valueOf(receivedTime));

        message = messageBuilder.build();
        messages.add(message);
      }
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    } finally {
      cursor.close();
    }

    return messages;
  }
}

