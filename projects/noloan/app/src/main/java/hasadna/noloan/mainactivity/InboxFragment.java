package hasadna.noloan.mainactivity;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import hasadna.noloan.common.SmsMessages;
import hasadna.noloan.InboxRecyclerAdapter;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import io.opencensus.tags.Tag;
import noloan.R;

public class InboxFragment extends Fragment {

  private OnFragmentInteractionListener fragmentInteractionListener;
  private RecyclerView recyclerView;
  private InboxRecyclerAdapter inboxRecyclerAdapter;

  public InboxFragment() {
    // Required empty public constructor
  }

  public static InboxFragment newInstance() {
    return new InboxFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    inboxRecyclerAdapter = new InboxRecyclerAdapter();

    // If new spams added to the DB - Check if the spams exists in user's inbox, if so update the
    // counter of suggesters
    SmsMessages.get()
        .setMessagesListener(
            new SmsMessages.MessagesListener() {
              @Override
              public void messageAdded(SmsMessage smsMessage) {
                // Check if user has this message in the inbox
                if (SmsMessages.get().searchInboxMessage(smsMessage) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  SmsMessages.get().searchInboxMessage(smsMessage)));
                }
              }

              @Override
              public void messageModified(int index) {
                int inboxIndex =
                    SmsMessages.get()
                        .searchInboxMessage(SmsMessages.get().getDbMessages().get(index));
                if (inboxIndex != -1) {
                  getActivity()
                      .runOnUiThread(() -> inboxRecyclerAdapter.notifyItemChanged(inboxIndex));
                }
              }

              @Override
              public void messageRemoved(int index, SmsMessage smsMessage) {
                if (SmsMessages.get().searchInboxMessage(smsMessage) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  SmsMessages.get().searchInboxMessage(smsMessage)));
                }
              }
            });

    inboxRecyclerAdapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeChanged(int positionStart, int itemCount) {}
        });
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inbox, container, false);

    recyclerView = rootView.findViewById(R.id.RecyclerView_inboxMessages);
    recyclerView.setRotationY(180);
    recyclerView.setAdapter(inboxRecyclerAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    // region SearchView Preferences
    SearchView searchView = rootView.findViewById(R.id.searchView_Inbox);
    searchView.setRotationY(-180);

    // Close keyboard when user clicks 'X' in searchView
    int searchCloseButtonId =
        searchView
            .getContext()
            .getResources()
            .getIdentifier("android:id/search_close_btn", null, null);
    ImageView closeButton = (ImageView) searchView.findViewById(searchCloseButtonId);
    closeButton.setOnClickListener(
        v -> {
          searchView.setQuery("", false);
          searchView.clearFocus();
        });

    // Remove default underline for searchView
    int searchPlateId =
        searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
    View searchPlate = searchView.findViewById(searchPlateId);
    if (searchPlate != null) {
      searchPlate.setBackgroundColor(Color.TRANSPARENT);
      searchPlate
          .getContext()
          .getResources()
          .getIdentifier("android:id/search_src_text", null, null);
    }

    // Handle query
    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            searchView.setQuery(query, false);
            searchView.clearFocus();
            return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            inboxRecyclerAdapter.getFilter().filter(newText);
            return true;
          }
        });
    // endregion searchView Preferences

    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      fragmentInteractionListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(
          context.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    fragmentInteractionListener = null;
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}

