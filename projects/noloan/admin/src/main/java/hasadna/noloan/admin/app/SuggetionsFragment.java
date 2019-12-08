package hasadna.noloan.admin.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SuggetionsFragment extends Fragment {

  SuggestionRecyclerAdapter recyclerAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    recyclerAdapter = new SuggestionRecyclerAdapter();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.suggestion_fregment, container, false);

    RecyclerView recyclerView = rootView.findViewById(R.id.suggetion_recycler);
    recyclerView.setRotationY(180);

    recyclerView.setAdapter(recyclerAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    return rootView;
  }
}

