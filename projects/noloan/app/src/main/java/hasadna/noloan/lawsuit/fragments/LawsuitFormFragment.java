package hasadna.noloan.lawsuit.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Calendar;

import hasadna.noloan.lawsuit.LawsuitActivity;
import hasadna.noloan.protobuf.LawsuitProto.Lawsuit;
import noloan.R;

public class LawsuitFormFragment extends Fragment {
  private static final String TAG = "LawsuitFormFragment";

  LawsuitActivity lawsuitActivity;
  EditText receivedDate;
  TextInputEditText userId;
  DatePickerDialog datePickerDialog;
  Calendar calendar;
  Button confirmForm;

  private boolean formValidated = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lawsuitActivity = (LawsuitActivity) getActivity();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView =
        (ViewGroup) inflater.inflate(R.layout.lawsuit_fragment_form, container, false);

    userId = rootView.findViewById(R.id.EditText_userID);
    receivedDate = rootView.findViewById(R.id.EditText_receivedSpamDate);
    confirmForm = rootView.findViewById(R.id.Button_formFragment_next);

    // Validate user id number
    userId.setOnFocusChangeListener(
        (v, hasFocus) -> {
          if (userId.getText().length() != 0 && !validateIdNumber(userId.getText().toString())) {
            rootView.findViewById(R.id.textView_idValidationError).setVisibility(View.VISIBLE);
            formValidated = false;
          } else
            rootView.findViewById(R.id.textView_idValidationError).setVisibility(View.INVISIBLE);
          formValidated = true;
        });
    // Display court's detail in TextViews
    receivedDate.setText(lawsuitActivity.selectedSmsSpam.getReceivedAt());
    receivedDate.setOnClickListener(v -> displayDatePicker());
    // Create PDF and proceed to SummaryFragment (Get file's path with:
    // LawsuitActivity.getAbsFilename())
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
                ((EditText) getView().findViewById(R.id.EditText_userFirstName))
                    .getText()
                    .toString())
            .setLastName(
                ((EditText) getView().findViewById(R.id.EditText_userLastName))
                    .getText()
                    .toString())
            .setUserId(
                ((EditText) getView().findViewById(R.id.EditText_userID)).getText().toString())
            .setUserAddress(
                ((EditText) getView().findViewById(R.id.EditText_userAddress)).getText().toString())
            .setUserPhone(((EditText) getView().findViewById(R.id.userPhone)).getText().toString())

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
            .setSentHaser(((CheckBox) getView().findViewById(R.id.CheckBox_sentHaser)).isChecked())
            .setMoreThanFiveClaims(
                ((CheckBox) getView().findViewById(R.id.CheckBox_fiveLawsuits)).isChecked())
            .setClaimAmount(
                ((EditText) getView().findViewById(R.id.EditText_claimAmount)).getText().toString())
            .build();
  }

  private boolean validateIdNumber(String idNumber) {

    if (idNumber.length() > 9) return false;

    // Complement id number to 9 digits
    int[] id = new int[9];
    for (int i = 0; i < idNumber.length(); i++) {
      id[8 - i] = Integer.parseInt("" + idNumber.charAt(idNumber.length() - 1 - i));
    }
    // Validate sums of digits - https://www.excelmaster.co.il/2018/08/06/check_id_excel/
    int sum = 0;
    for (int i = 1; i < 9; i = i + 2) {
      id[i] = ((id[i] * 2) > 9) ? ((id[i] * 2) % 10 + (id[i] * 2) / 10) : id[i] * 2;
      sum += id[i - 1] + id[i];
    }
    sum += id[8];
    if (sum % 10 == 0) return true;
    else return false;
  }
}

