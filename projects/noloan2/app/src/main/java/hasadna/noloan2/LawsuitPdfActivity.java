package hasadna.noloan2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import noloan.R;

public class LawsuitPdfActivity extends AppCompatActivity {
  // TODO: Add logs.
  private static final String TAG = "LawsuitPdfActivity";
  private final int STORAGE_PERMISSION_CODE = 1;

  // filenames
  private static final String LAWSUIT_MAIN_DIR_NAME = "lawsuits";
  private static final String LAWSUIT_OUTPUT_DIR_NAME = "pdf";

  // absPaths
  private String lawsuitMainPath = "";
  private String lawsuitOutputPath = "";

  // Formats
  private static final String TIME_ZONE = "Asia/Jerusalem";
  private static final SimpleDateFormat DATE_TIME_FORMATTER =
          new SimpleDateFormat("dd-M-yyyy hh-mm-ss");
  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-M-yyyy");

  // Lawsuit optional fields
  private String moreThanFiveLawsuits = "הגיש";
  private String lessThanFiveLawsuits = "לא הגיש/ו";
  private String claimCaseHaser =
          "לאחר שהתובע חזר בו מהסכמתו לקבלת דבר/י פרסומת מהנתבע/ים, על-ידי משלוח הודעת סירוב כהגדרתה בחוק ";
  private String claimCaseSubscription =
          "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";

  EditText receivedDate;
  DatePickerDialog datePickerDialog;
  Calendar calendar;
  Button createPdfButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lawsuit_pdf);
    initDirStructure();
    setTimeZones();

    createPdfButton = findViewById(R.id.button_createPDF);
    receivedDate = findViewById(R.id.editText_spamDate);

    createPdfButton.setOnClickListener(v -> checkPermissionsThenCreatePdf());
    receivedDate.setOnClickListener(v -> displayDatePicker());
  }

  // Save file to device, then ask in AlertDialog if to share
  private void createAndSharePdf() {
    LicenseKey.loadLicenseFile(getResources().openRawResource(R.raw.itextkey));
    String path = Paths.get(lawsuitOutputPath, getPdfFilename()).toString();

    try {
      HtmlConverter.convertToPdf(fillTemplate(), new FileOutputStream(path));
      sharePdf(path);
      // Exceptions: read template / write output
    } catch (IOException e) {
      Log.w(TAG, "Read template / Write file: " + e.getMessage());
      e.printStackTrace();
      Toast.makeText(LawsuitPdfActivity.this, "Failed to create lawsuit.", Toast.LENGTH_LONG)
              .show();
    }
  }

  private void sharePdf(String path) {
    new AlertDialog.Builder(this, R.style.AlertDialog)
            .setTitle("כתב תביעה נוצר")
            .setMessage("כתב התביעה נוצר ונשמר במכשירך.\n האם תרצה/י לשתף?")
            .setPositiveButton(
                    "כן",
                    (dialog, which) -> {
                      Uri uri =
                              FileProvider.getUriForFile(
                                      getApplicationContext(), getPackageName()+ ".fileprovider", new File(path));
                      Intent shareIntent = new Intent();
                      shareIntent.setAction(Intent.ACTION_SEND);
                      shareIntent.setType("application/pdf");
                      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                      startActivity(Intent.createChooser(shareIntent, "Share via"));
                    })
            .setNegativeButton("לא", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
  }

  private String fillTemplate() throws IOException {
    // TODO:
    String lawsuit = null;
    try {
      InputStream inputStream = getAssets().open("template.xhtml");
      byte[] buffer = new byte[inputStream.available()];
      inputStream.read(buffer);
      inputStream.close();
      lawsuit = new String(buffer, Charset.forName("UTF-8"));
    } catch (IOException e) {
      throw new IOException("Read template error.");
    }

    // General form fields
    lawsuit =
            lawsuit.replace(
                    "&lt;claimCase&gt;",
                    ((CheckBox) findViewById(R.id.checkBox_sentHaser)).isChecked()
                            ? claimCaseHaser
                            : claimCaseSubscription);
    lawsuit =
            lawsuit.replace(
                    "&lt;claimAmount&gt;",
                    ((EditText) findViewById(R.id.claimAmount)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;moreThanFiveLawsuits&gt;",
                    ((CheckBox) findViewById(R.id.checkbox_fiveLawsuits)).isChecked()
                            ? moreThanFiveLawsuits
                            : lessThanFiveLawsuits);
    lawsuit = lawsuit.replace("&lt;undefined&gt;", "");
    lawsuit = lawsuit.replace("&lt;receivedSpamDate&gt;", receivedDate.getText().toString());
    lawsuit = lawsuit.replace("&lt;currentDate&gt;", DATE_FORMATTER.format(new Date()));

    // User fields
    lawsuit =
            lawsuit.replace(
                    "&lt;userFullName&gt;",
                    ((EditText) findViewById(R.id.userPrivateName)).getText().toString()
                            + " "
                            + ((EditText) findViewById(R.id.userLastName)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;userId&gt;", ((EditText) findViewById(R.id.userID)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;userAddress&gt;",
                    ((EditText) findViewById(R.id.userAddress)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;userPhone&gt;", ((EditText) findViewById(R.id.userPhone)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;userFax&gt;", ((EditText) findViewById(R.id.userFax)).getText().toString());

    // Spam company
    lawsuit =
            lawsuit.replace(
                    "&lt;companyName&gt;",
                    ((EditText) findViewById(R.id.companyName)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;companyId&gt;", ((EditText) findViewById(R.id.companyId)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;companyAddress&gt;",
                    ((EditText) findViewById(R.id.companyAddress)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;companyPhone&gt;",
                    ((EditText) findViewById(R.id.companyPhone)).getText().toString());
    lawsuit =
            lawsuit.replace(
                    "&lt;companyFax&gt;", ((EditText) findViewById(R.id.companyFax)).getText().toString());

    return lawsuit;
  }

  private String getPdfFilename() {
    return DATE_TIME_FORMATTER.format(new Date()) + ".pdf";
  }

  private void initDirStructure() {
    // TODO: Paths.get() require API 26. Find/Create function that suits lower API versions.
    lawsuitMainPath =
            Paths.get(
                    Environment.getExternalStorageDirectory().getPath(),
                    getString(R.string.app_name),
                    LAWSUIT_MAIN_DIR_NAME)
                    .toString();
    makeDir(lawsuitMainPath);

    // Output
    lawsuitOutputPath = Paths.get(lawsuitMainPath, LAWSUIT_OUTPUT_DIR_NAME).toString();
    makeDir(lawsuitOutputPath);
  }

  private void makeDir(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  private void setTimeZones() {
    DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
    DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
  }

  private void checkPermissionsThenCreatePdf() {
    // Ask runtime permissions for devices running SDK > 22
    if (Build.VERSION.SDK_INT >= 23) {

      // Permission granted
      if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED)) {
        createAndSharePdf();
      }
      // If user denied permission before, show explanation dialog before requesting
      else {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

          new AlertDialog.Builder(this)
                  .setTitle("Permission needed")
                  .setMessage("File access permission is needed for creating lawsuit PDF.")
                  .setPositiveButton(
                          "ok",
                          (dialog, which) ->
                                  ActivityCompat.requestPermissions(
                                          LawsuitPdfActivity.this,
                                          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                          STORAGE_PERMISSION_CODE))
                  .setNegativeButton(
                          "cancel",
                          (dialog, which) -> {
                            Toast.makeText(
                                    LawsuitPdfActivity.this,
                                    "Permission is needed to create PDF.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            dialog.dismiss();
                          })
                  .create()
                  .show();

        } else {
          ActivityCompat.requestPermissions(
                  this,
                  new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                  STORAGE_PERMISSION_CODE);
        }
      }
      // Devices running SDK <= 22, permissions granted on installation
    } else {
      createAndSharePdf();
    }
  }

  private void displayDatePicker() {
    calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);

    datePickerDialog =
            new /**/ DatePickerDialog(
                    LawsuitPdfActivity.this,
                    (datePicker, currentYear, currentMonth, currentDay) ->
                            receivedDate.setText(day + "/" + (month + 1) + "/" + year),
                    year,
                    month,
                    day);
    datePickerDialog.show();
  }

  // Call createAndSharePdf() if permission granted
  @Override
  public void onRequestPermissionsResult(
          int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == STORAGE_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        createAndSharePdf();
      } else {
        Toast.makeText(
                LawsuitPdfActivity.this, "Permission is needed to create PDF.", Toast.LENGTH_LONG)
                .show();
      }
    }
  }
}
