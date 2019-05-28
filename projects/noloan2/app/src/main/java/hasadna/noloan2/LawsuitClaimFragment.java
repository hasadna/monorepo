package hasadna.noloan2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import javax.annotation.Nullable;

import noloan.R;

public class LawsuitClaimFragment extends Fragment {
  private static final String TAG = "LawsuitClaimFragment";

  Button sendOnline;
  TextView courtName;
  TextView courtAddress;
  TextView courtFax;
  TextView selectCourt;

  public LawsuitClaimFragment() {
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
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_claim, container, false);

    // Link user to נט המשפט
    sendOnline = rootView.findViewById(R.id.button_claimOnline);
    sendOnline.setOnClickListener(
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
    selectCourt.setOnClickListener(v -> ((LawsuitActivity) getActivity()).displayCourtPicker(this));

    // If court had been chosen before, display court's details
    if (((LawsuitActivity) getActivity()).selectedCourt != null) {
      courtName.setText(
          "בית המשפט לתביעות קטנות - " + ((LawsuitActivity) getActivity()).selectedCourt.getName());
      courtAddress.setText(((LawsuitActivity) getActivity()).selectedCourt.getAddress());
      courtFax.setText(((LawsuitActivity) getActivity()).selectedCourt.getFax());
    }

    return rootView;
  }
}

