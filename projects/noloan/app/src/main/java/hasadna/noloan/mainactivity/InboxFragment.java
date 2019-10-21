package hasadna.noloan.mainactivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hasadna.noloan.Messages;
import hasadna.noloan.InboxRecyclerAdapter;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
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
    Messages.getInstance()
        .setMessagesListener(
            new Messages.MessagesListener() {
              @Override
              public void messageAdded(SmsMessage smsMessage) {
                // Check if user has this message in the inbox
                if (Messages.getInstance().searchInboxMessage(smsMessage) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  Messages.getInstance().searchInboxMessage(smsMessage)));
                }
              }

              @Override
              public void messageModified(int index) {
                if (Messages.getInstance()
                        .searchInboxMessage(Messages.getInstance().getDbMessages().get(index))
                    != -1) {
                  getActivity().runOnUiThread(() -> inboxRecyclerAdapter.notifyItemChanged(index));
                }
              }

              @Override
              public void messageRemoved(int index, SmsMessage smsMessage) {
                if (Messages.getInstance().searchInboxMessage(smsMessage) != -1) {
                  getActivity()
                      .runOnUiThread(
                          () ->
                              inboxRecyclerAdapter.notifyItemChanged(
                                  Messages.getInstance().searchInboxMessage(smsMessage)));
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

