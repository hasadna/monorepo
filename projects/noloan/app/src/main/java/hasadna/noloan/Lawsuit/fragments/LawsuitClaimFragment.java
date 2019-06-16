package hasadna.noloan.lawsuit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hasadna.noloan.lawsuit.LawsuitActivity;
import noloan.R;

public class LawsuitClaimFragment extends Fragment {
  private static final String TAG = "LawsuitClaimFragment";

  LawsuitActivity lawsuitActivity;
  Button claimOnline;
  TextView courtName;
  TextView courtAddress;
  TextView courtFax;
  TextView selectCourt;

  public LawsuitClaimFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lawsuitActivity = (LawsuitActivity) getActivity();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_claim, container, false);

    // Link user to נט המשפט
    claimOnline = rootView.findViewById(R.id.button_claimOnline);
    claimOnline.setOnClickListener(
        v -> {
          Uri uri = Uri.parse(getString(R.string.lawsuit_claim_online_website_link));
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(intent);
        });

    // Details for sending via fax / mail
    courtName = rootView.findViewById(R.id.textView_ClaimFragment_courtName);
    courtAddress = rootView.findViewById(R.id.textView_ClaimFragment_courtAddress);
    courtFax = rootView.findViewById(R.id.textView_ClaimFragment_courtFax);

    // Choose court
    selectCourt = rootView.findViewById(R.id.textView_ClaimFragment_selectCourt);
    selectCourt.setOnClickListener(v -> lawsuitActivity.displayCourtPicker(this));

    // If court had been chosen before, display court's details
    if (lawsuitActivity.selectedCourt != null) {
      courtName.setText("בית המשפט לתביעות קטנות - " + lawsuitActivity.selectedCourt.getName());
      courtAddress.setText(lawsuitActivity.selectedCourt.getAddress());
      courtFax.setText(lawsuitActivity.selectedCourt.getFax());
    }

    return rootView;
  }
}

