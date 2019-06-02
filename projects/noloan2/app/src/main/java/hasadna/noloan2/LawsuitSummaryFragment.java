package hasadna.noloan2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import noloan.R;

public class LawsuitSummaryFragment extends Fragment {
  private static final String TAG = "LawsuitSummaryFragment";

  LawsuitActivity lawsuitActivity;
  Button shareSave;
  Button claimLawsuit;

  public LawsuitSummaryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lawsuitActivity = (LawsuitActivity)getActivity();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_summary, container, false);

    shareSave = rootView.findViewById(R.id.button_claimFragment_shareSave);
    claimLawsuit = rootView.findViewById(R.id.button_claimFragment_claim);

    // Pops share/save dialog
    shareSave.setOnClickListener(v -> lawsuitActivity.sharePdf());
    // Proceed to ClaimFragment
    claimLawsuit.setOnClickListener(
        v -> lawsuitActivity.viewPager.setCurrentItem(2, false));

    return rootView;
  }
}

