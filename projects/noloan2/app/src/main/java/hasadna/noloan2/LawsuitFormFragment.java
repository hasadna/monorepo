package hasadna.noloan2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;

import hasadna.noloan2.protobuf.LawsuitProto.Lawsuit;
import noloan.R;

public class LawsuitFormFragment extends Fragment {
  private static final String TAG = "LawsuitFormFragment";

  LawsuitActivity lawsuitActivity;
  EditText receivedDate;
  DatePickerDialog datePickerDialog;
  Calendar calendar;
  Button confirmForm;
  TextView selectCourt;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lawsuitActivity = (LawsuitActivity)getActivity();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_form, container, false);

    receivedDate = rootView.findViewById(R.id.EditText_receivedSpamDate);
    selectCourt = rootView.findViewById(R.id.textView_selectCourt_FormFragment);
    confirmForm = rootView.findViewById(R.id.button_formFragment_next);

    // If court was already selected, display its name
    receivedDate.setText(lawsuitActivity.selectedSmsSpam.getReceivedAt());
    receivedDate.setOnClickListener(v -> displayDatePicker());
    if (lawsuitActivity.selectedCourt != null) {
      selectCourt.setText(
              "בית המשפט לתביעות קטנות - " + lawsuitActivity.selectedCourt.getName());
    }
    selectCourt.setOnClickListener(v -> lawsuitActivity.displayCourtPicker(this));

    // Create PDF and proceed to SummaryFragment
    confirmForm.setOnClickListener(
        v -> {
          createLawsuitProto();
          lawsuitActivity.createPdf();
          lawsuitActivity.viewPager.setCurrentItem(1, false);
        });

    return rootView;
  }

  // Pops a calendar dialog when clicking on date fields
  private void displayDatePicker() {
    calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);

    datePickerDialog =
        new DatePickerDialog(
                lawsuitActivity,
            (datePicker, currentYear, currentMonth, currentDay) ->
                receivedDate.setText(day + "/" + (month + 1) + "/" + year),
            year,
            month,
            day);
    datePickerDialog.show();
  }

  // Update LawsuitProto with form's fields
  private void createLawsuitProto() {
    lawsuitActivity.lawsuitProto =
        Lawsuit.newBuilder()
            // User
            .setFirstName(
                ((EditText) getView().findViewById(R.id.userPrivateName)).getText().toString())
            .setLastName(
                ((EditText) getView().findViewById(R.id.userLastName)).getText().toString())
            .setUserId(((EditText) getView().findViewById(R.id.userID)).getText().toString())
            .setUserAddress(
                ((EditText) getView().findViewById(R.id.userAddress)).getText().toString())
            .setUserPhone(((EditText) getView().findViewById(R.id.userPhone)).getText().toString())
            .setUserFax(((EditText) getView().findViewById(R.id.userFax)).getText().toString())

            // Company
            .setCompanyName(
                ((EditText) getView().findViewById(R.id.EditText_companyName)).getText().toString())
            .setCompanyId(
                ((EditText) getView().findViewById(R.id.EditText_companyId)).getText().toString())
            .setCompanyAddress(
                ((EditText) getView().findViewById(R.id.EditText_companyAddress))
                    .getText()
                    .toString())
            .setCompanyPhone(
                ((EditText) getView().findViewById(R.id.EditText_companyPhone))
                    .getText()
                    .toString())
            .setCompanyFax(
                ((EditText) getView().findViewById(R.id.EditText_companyFax)).getText().toString())

            // General
            .setDateReceived(
                ((EditText) getView().findViewById(R.id.EditText_receivedSpamDate))
                    .getText()
                    .toString())
            .setSentHaser(((CheckBox) getView().findViewById(R.id.checkBox_sentHaser)).isChecked())
            .setMoreThanFiveClaims(
                ((CheckBox) getView().findViewById(R.id.checkbox_fiveLawsuits)).isChecked())
            .setClaimAmount(
                ((EditText) getView().findViewById(R.id.EditText_claimAmount)).getText().toString())
            .build();
  }
}

