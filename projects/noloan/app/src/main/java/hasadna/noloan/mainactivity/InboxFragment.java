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

import java.util.List;

import hasadna.noloan.SmsRecyclerAdapter;
import hasadna.noloan.protobuf.SmsProto;
import noloan.R;

public class InboxFragment extends Fragment {

  private OnFragmentInteractionListener fragmentInteractionListener;
  private RecyclerView recyclerView;
  private SmsRecyclerAdapter smsAdapter;

  public InboxFragment() {
    // Required empty public constructor
  }

  public static InboxFragment newInstance() {
    return new InboxFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    List<SmsProto.SmsMessage> messages = ((MainActivity) getActivity()).readSmsFromDevice();
    smsAdapter = new SmsRecyclerAdapter(messages);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_inbox, container, false);

    recyclerView = rootView.findViewById(R.id.RecyclerView_inboxMessages);
    recyclerView.setRotationY(180);

    recyclerView.setAdapter(smsAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    return rootView;
  }

  public void onButtonPressed(Uri uri) {
    if (fragmentInteractionListener != null) {
      fragmentInteractionListener.onFragmentInteraction(uri);
    }
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

