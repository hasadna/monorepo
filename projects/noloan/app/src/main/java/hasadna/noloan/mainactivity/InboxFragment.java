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

  private OnFragmentInteractionListener mListener;
  private RecyclerView recyclerView;
  private SmsRecyclerAdapter smsAdapter;

  public InboxFragment() {
    // Required empty public constructor
  }

  // TODO: Rename and change types and number of parameters
  public static InboxFragment newInstance() {
    InboxFragment fragment = new InboxFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
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

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(
          context.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this fragment to allow an
   * interaction in this fragment to be communicated to the activity and potentially other fragments
   * contained in that activity.
   *
   * <p>See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with
   * Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}

