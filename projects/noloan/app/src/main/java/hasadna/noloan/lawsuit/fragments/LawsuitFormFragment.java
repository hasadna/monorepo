package hasadna.noloan.lawsuit.fragments;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Calendar;

import hasadna.noloan.lawsuit.LawsuitActivity;
import noloan.R;

public class LawsuitFormFragment extends Fragment {
  private static final String TAG = "LawsuitFormFragment";

  LawsuitActivity lawsuitActivity;
  EditText receivedDate;
  TextInputEditText userId;
  TextView idValidationError;
  Button searchCompany;
  DatePickerDialog datePickerDialog;
  Calendar calendar;
  Button confirmForm;
  ScrollView scrollView;
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
    idValidationError = rootView.findViewById(R.id.textView_idValidationError);
    receivedDate = rootView.findViewById(R.id.EditText_receivedSpamDate);
    confirmForm = rootView.findViewById(R.id.Button_formFragment_next);
    scrollView = rootView.findViewById(R.id.ScrollView_formFragment);
    searchCompany = rootView.findViewById(R.id.button_formFragment_searchCompany);
    searchCompany.setOnClickListener(
        v -> {
          Uri uri = Uri.parse(getString(R.string.formFragment_searchCorporations_website));
          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          startActivity(intent);
        });
    // Validate user id number
    userId.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void afterTextChanged(Editable s) {
            validateIdNumber(userId.getText().toString());
          }

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    receivedDate.setText(lawsuitActivity.spamMessage.getReceivedAt());
    receivedDate.setOnClickListener(v -> displayDatePicker());

    // Proceed to next fragment only if form is validated (user's id number)
    confirmForm.setOnClickListener(
        v -> {
          if (formValidated) {
            // Create PDF and proceed to SummaryFragment (Get file's path with:
            // LawsuitActivity.getAbsFilename())
            createLawsuitProto();
            lawsuitActivity.updateSharedPreferences();
            lawsuitActivity.createPdf();
            lawsuitActivity.viewPager.setCurrentItem(1, false);
          } else {
            validateIdNumber(userId.getText().toString());
            ObjectAnimator.ofInt(scrollView, "scrollY", userId.getScrollY())
                .setDuration(800)
                .start();
            userId.requestFocus();
          }
        });

    return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    updatePreviousFormValues();
  }

  @Override
  public void onPause() {
    super.onPause();
    createLawsuitProto();
    lawsuitActivity.updateSharedPreferences();
  }

  private void updatePreviousFormValues() {

    // User
    ((EditText) getView().findViewById(R.id.EditText_userFirstName))
        .setText(lawsuitActivity.lawsuitProto.getFirstName());
    ((EditText) getView().findViewById(R.id.EditText_userLastName))
        .setText(lawsuitActivity.lawsuitProto.getLastName());
    ((EditText) getView().findViewById(R.id.EditText_userID))
        .setText(lawsuitActivity.lawsuitProto.getUserId());
    ((EditText) getView().findViewById(R.id.EditText_userAddress))
        .setText(lawsuitActivity.lawsuitProto.getUserAddress());
    ((EditText) getView().findViewById(R.id.EditText_userPhone))
        .setText(lawsuitActivity.lawsuitProto.getUserPhone());

    // Company
    ((EditText) getView().findViewById(R.id.EditText_companyName))
        .setText(lawsuitActivity.lawsuitProto.getCompanyName());
    ((EditText) getView().findViewById(R.id.EditText_companyId))
        .setText(lawsuitActivity.lawsuitProto.getCompanyId());
    ((EditText) getView().findViewById(R.id.EditText_companyAddress))
        .setText(lawsuitActivity.lawsuitProto.getCompanyAddress());
    ((EditText) getView().findViewById(R.id.EditText_companyPhone))
        .setText(lawsuitActivity.lawsuitProto.getCompanyPhone());
    ((EditText) getView().findViewById(R.id.EditText_companyFax))
        .setText(lawsuitActivity.lawsuitProto.getCompanyFax());

    // General
    ((EditText) getView().findViewById(R.id.EditText_receivedSpamDate))
        .setText(lawsuitActivity.lawsuitProto.getDateReceived());
    ((EditText) getView().findViewById(R.id.EditText_claimAmount))
        .setText(lawsuitActivity.lawsuitProto.getClaimAmount());
    ((CheckBox) getView().findViewById(R.id.CheckBox_sentHaser))
        .setChecked(lawsuitActivity.lawsuitProto.getSentHaser());
    ((CheckBox) getView().findViewById(R.id.CheckBox_fiveLawsuits))
        .setChecked(lawsuitActivity.lawsuitProto.getMoreThanFiveClaims());
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
        lawsuitActivity
            .lawsuitProto
            .toBuilder()

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
            .setUserPhone(
                ((EditText) getView().findViewById(R.id.EditText_userPhone)).getText().toString())

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

  // Flags 'formValidated' which is been checked before proceeding to next fragment
  private boolean validateIdNumber(String idNumber) {
    if (idNumber.length() > 9 || idNumber.length() < 4) {
      idValidationError.setVisibility(View.VISIBLE);
      formValidated = false;
      return false;
    }
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
    if (sum % 10 == 0) {
      idValidationError.setVisibility(View.GONE);
      formValidated = true;
      return true;
    } else {
      idValidationError.setVisibility(View.VISIBLE);
      formValidated = false;
      return false;
    }
  }
}

