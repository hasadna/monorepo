package hasadna.noloan.admin.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SpamFragment extends Fragment {

  SpamRecyclerAdapter spamRecyclerAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    spamRecyclerAdapter = new SpamRecyclerAdapter();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.spam_fregment, container, false);

    RecyclerView recyclerView = rootView.findViewById(R.id.spam_recycler);
    recyclerView.setRotationY(180);

    recyclerView.setAdapter(spamRecyclerAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    return rootView;
  }
}

