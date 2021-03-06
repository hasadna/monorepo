package hasadna.noloan.mainactivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hasadna.noloan.SpamRecyclerAdapter;
import noloan.R;

public class SpamFragment extends Fragment {
  static final String TAG = "SpamFragment";

  private OnFragmentInteractionListener fragmentInteractionListener;
  private RecyclerView recyclerView;
  private SpamRecyclerAdapter spamRecyclerAdapter;

  public SpamFragment() {
    spamRecyclerAdapter = new SpamRecyclerAdapter();
  }

  public static InboxFragment newInstance() {
    return new InboxFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    spamRecyclerAdapter = new SpamRecyclerAdapter();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_spam, container, false);

    recyclerView = rootView.findViewById(R.id.RecyclerView_spamMessages);
    recyclerView.setRotationY(180);
    spamRecyclerAdapter.registerAdapterDataObserver(
        new RecyclerView.AdapterDataObserver() {
          @Override
          public void onItemRangeInserted(int positionStart, int itemCount) {
            ((MainActivity) getActivity()).updateTitles();
          }

          @Override
          public void onItemRangeRemoved(int positionStart, int itemCount) {
            ((MainActivity) getActivity()).updateTitles();
          }
        });

    recyclerView.setAdapter(spamRecyclerAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    ((MainActivity) getActivity()).updateTitles();

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

  int getSpamCount() {
    return spamRecyclerAdapter.getItemCount();
  }

  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }
}

