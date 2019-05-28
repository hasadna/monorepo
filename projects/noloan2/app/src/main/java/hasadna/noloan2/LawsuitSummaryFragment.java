package hasadna.noloan2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;

import noloan.R;

public class LawsuitSummaryFragment extends Fragment {
  private static final String TAG = "LawsuitSummaryFragment";

  Button shareSaveButton;
  Button claimButton;

  public LawsuitSummaryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_summary, container, false);

    shareSaveButton = rootView.findViewById(R.id.button_claimFragment_shareSave);
    claimButton = rootView.findViewById(R.id.button_claimFragment_claim);

    // Pops share/save dialog
    shareSaveButton.setOnClickListener(v -> ((LawsuitActivity) getActivity()).sharePdf());
    // Proceed to ClaimFragment
    claimButton.setOnClickListener(
        v -> ((LawsuitActivity) getActivity()).viewPager.setCurrentItem(2, false));

    return rootView;
  }
}

