package hasadna.noloan.lawsuit.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.CourtProto;
import noloan.R;

public class LawsuitSummaryFragment extends Fragment {
  private static final String TAG = "LawsuitSummaryFragment";

  LawsuitActivity lawsuitActivity;
  Button claimOnline;
  Button viewFile;
  ImageButton saveFile;
  ImageButton shareFile;
  Button selectCourt;
  TextView courtAddress;
  TextView courtFax;
  ArrayList<CourtProto.Court> courtList = null;
  AlertDialog.Builder courtBuilder;
  public CourtProto.Court selectedCourt;

  public LawsuitSummaryFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lawsuitActivity = (LawsuitActivity) getActivity();
    courtList = initCourts();
    courtBuilder = new AlertDialog.Builder(getContext());
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_summary, container, false);

    // View / Save / Share
    viewFile = rootView.findViewById(R.id.button_viewFile);
    saveFile = rootView.findViewById(R.id.button_saveFile);
    shareFile = rootView.findViewById(R.id.button_shareFile);
    viewFile.setOnClickListener(v -> viewPDFIntent());
    saveFile.setOnClickListener(v -> savePDFIntent());
    shareFile.setOnClickListener(v -> sharePDFIntent());

    // Link to נט המשפט
    claimOnline = rootView.findViewById(R.id.button_claimOnline);
    claimOnline.setOnClickListener(
        v -> {
          Uri uri = Uri.parse(getString(R.string.lawsuit_claim_online_website_link));
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(intent);
        });

    // Display details for sending via fax / mail
    selectCourt = rootView.findViewById(R.id.button_selectCourt);
    selectCourt.setOnClickListener(v -> displayCourtPicker());
    courtAddress = rootView.findViewById(R.id.textView_courtAddress);
    courtFax = rootView.findViewById(R.id.textView_courtFax);

    return rootView;
  }

  public void viewPDFIntent() {
    if (new File(lawsuitActivity.getAbsFilename()).exists()) {
      Uri uri =
          FileProvider.getUriForFile(
              getContext(),
              lawsuitActivity.getPackageName() + ".fileprovider",
              new File(lawsuitActivity.getAbsFilename()));
      Intent intentExport = new Intent();
      intentExport.setAction(Intent.ACTION_VIEW);
      intentExport.setDataAndType(uri, "application/pdf");
      intentExport.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      // Try viewing file, if no app for viewing PDFs found - direct user to appStore
      try {
        startActivity(
            Intent.createChooser(
                intentExport, getString(R.string.summaryFragment_alertDialog_viewTitle)));
      } catch (ActivityNotFoundException e) {
        Toast.makeText(getContext(), "There's no program to open this file", Toast.LENGTH_LONG)
            .show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(
            Uri.parse("https://play.google.com/store/apps/details?id=com.example.android"));
        intent.setPackage("com.android.vending");
        startActivity(intent);
      }
    } else {
      Log.w(
          TAG,
          "Could not view pdf, file doesn't exists.\nabsFilename: "
              + lawsuitActivity.getAbsFilename());
    }
  }

  // TODO: Add to intent option for "Saving as" with ESExplorer / Other file management apps
  public void savePDFIntent() {
    if (new File(lawsuitActivity.getAbsFilename()).exists()) {
      Uri uri =
          FileProvider.getUriForFile(
              getContext(),
              lawsuitActivity.getPackageName() + ".fileprovider",
              new File(lawsuitActivity.getAbsFilename()));
      Intent shareIntent =
          ShareCompat.IntentBuilder.from(lawsuitActivity)
              .setText("Share PDF doc")
              .setType("application/pdf")
              .setStream(uri)
              .getIntent()
              .setPackage("com.google.android.apps.docs");
      try {
        startActivity(shareIntent);
      } catch (ActivityNotFoundException e) {
        Toast.makeText(getContext(), "There's no program to save this file", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  public void sharePDFIntent() {
    if (new File(lawsuitActivity.getAbsFilename()).exists()) {
      Uri uri =
          FileProvider.getUriForFile(
              getContext(),
              lawsuitActivity.getPackageName() + ".fileprovider",
              new File(lawsuitActivity.getAbsFilename()));
      Intent intentShare = new Intent();
      intentShare.setAction(Intent.ACTION_SEND);
      intentShare.setType("application/pdf");
      intentShare.putExtra(Intent.EXTRA_STREAM, uri);
      intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      startActivity(Intent.createChooser(intentShare, "שתף קובץ תביעה דרך"));
    } else {
      Log.w(
          TAG,
          "Could not share pdf, file doesn't exists.\nabsFilename: "
              + lawsuitActivity.getAbsFilename());
    }
  }

  // Create an ArrayList of courthouses Protos from res/values/courtHouses.xml
  // TODO: Change courtHouses to prototxt
  public ArrayList<CourtProto.Court> initCourts() {
    ArrayList<CourtProto.Court> courts = new ArrayList<>(28);
    String[] courtList = getResources().getStringArray(R.array.courts_detailed);
    String[] courtDetail;

    for (int i = 0; i < courtList.length; i++) {
      courtDetail = courtList[i].split("\\|");
      CourtProto.Court court =
          CourtProto.Court.newBuilder()
              .setName(courtDetail[0])
              .setAddress(courtDetail[1])
              .setFax(courtDetail[2])
              .setWebsite(courtDetail[3])
              .build();
      courts.add(court);
    }
    return courts;
  }

  // Choose court alert dialog
  public void displayCourtPicker() {
    // List courts
    String[] courtNames = new String[28];
    int index = 0;
    for (CourtProto.Court court : courtList) {
      courtNames[index++] = court.getName();
    }

    // Build dialog
    TextView title = new TextView(getContext());
    title.setText("בחירת בית משפט");
    title.setGravity(Gravity.CENTER);
    title.setTextSize(22);
    title.setPadding(0, 15, 0, 0);
    title.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    courtBuilder.setCustomTitle(title);
    courtBuilder.setItems(
        courtNames,
        (dialog, which) -> {
          selectedCourt = courtList.get(which);
          selectCourt.setText("בית המשפט לתביעות קטנות - " + selectedCourt.getName());
          courtAddress.setText("כתובת:" + selectedCourt.getAddress());
          courtFax.setText("פקס:" + selectedCourt.getFax());
          courtAddress.setVisibility(TextView.VISIBLE);
          courtFax.setVisibility(TextView.VISIBLE);
        });
    courtBuilder.show();
  }
}

