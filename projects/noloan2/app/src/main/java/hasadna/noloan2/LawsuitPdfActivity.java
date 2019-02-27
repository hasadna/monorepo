package hasadna.noloan2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import noloan.R;

public class LawsuitPdfActivity extends AppCompatActivity {
  // TODO: Add logs.
  private static final String TAG = "LawsuitPdfActivity";
  private final int STORAGE_PERMISSION_CODE = 1;

  // filenames
  private static final String LAWSUIT_MAIN_DIR_NAME = "lawsuits";
  private static final String LAWSUIT_TEMPLATE_DIR_NAME = "templates";
  private static final String LAWSUIT_OUTPUT_DIR_NAME = "pdf";
  private static final String LAWSUIT_TEMPLATE_FILE_NAME = "template.xhtml";

  // absPaths
  private String lawsuitMainPath = "";
  private String lawsuitTemplateDirPath = "";
  private String lawsuitTemplateFilePath = "";
  private String lawsuitOutputPath = "";

  // Formats
  private static final String TIME_ZONE = "Asia/Jerusalem";
  private static final SimpleDateFormat DATE_TIME_FORMATTER =
      new SimpleDateFormat("dd-M-yyyy hh-mm-ss");
  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-M-yyyy");

  // TODO: Remove these once client-side form is done.
  private boolean sentHaser = true;
  private boolean sentMoreThanFiveLawsuits = false; // The past year
  private Date receivedSpamDate = new Date();

  // region Lawsuit form fields
  // General
  private String spamType = "הודעה אלקטרונית";
  private String claimAmount = "סכום תביעה";
  private String moreThanFiveLawsuits = "הגיש";
  private String lessThanFiveLawsuits = "לא הגיש/ו";
  private String claimCaseHaser = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
  private String claimCaseSubscription =
      "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";

  // User
  private String userPrivateName = "שם פרטי";
  private String userLastName = "שם משפחה";
  private String userID = "ת ז משתמש";
  private String userAddress = "כתובת משתמש";
  private String userPhone = "טלפון משתמש";
  private String userFax = "פקס משתמש";

  // First spam company's details
  private String companyName = "שם החברה";
  private String companyId = "מספר ח.פ";
  private String companyAddress = "כתובת החברה";
  private String companyPhone = "טלפון החברה";
  private String companyFax = "פקס חברה";

  // Second spam company's details
  private String company2Name = "שם החברה 2";
  private String company2Id = "מספר ח.פ 2";
  private String company2Address = "כתובת החברה 2";
  private String company2Phone = "טלפון החברה 2";
  private String company2Fax = "פקס חברה 2";
  // endregion

  Button createPdfButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lawsuit_pdf);
    createPdfButton = (Button) findViewById(R.id.button_createPDF);

    initDirStructure();
    setTimeZones();

    createPdfButton.setOnClickListener((View v) -> checkPermissionsThenCreatePdf());
  }

  private void createPdf() {
    // TODO: Success reading license file only from "res/raw". Perhaps should be read from different
    // dir
    // Activate iText license
    LicenseKey.loadLicenseFile(getResources().openRawResource(R.raw.itextkey));
    try {
      HtmlConverter.convertToPdf(
          fillTemplate(),
          new FileOutputStream(Paths.get(lawsuitOutputPath, getPdfFilename()).toString()));
      Toast.makeText(LawsuitPdfActivity.this, "Lawsuit created!", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      Log.w(TAG, "Error creating PDF Lawsuit");
      e.printStackTrace();
    }
  }

  private String fillTemplate() {

    String lawsuit = null;
    try {
      lawsuit =
          new String(
              Files.readAllBytes(Paths.get(lawsuitTemplateFilePath)), StandardCharsets.UTF_8);
    } catch (IOException e) {
      Log.w(TAG, "Error reading lawsuit's template.");
      e.printStackTrace();
    }

    // General form fields
    lawsuit =
        lawsuit.replace("&lt;claimCase&gt;", sentHaser ? claimCaseHaser : claimCaseSubscription);
    lawsuit = lawsuit.replace("&lt;claimAmount&gt;", claimAmount);
    lawsuit = lawsuit.replace("&lt;spamType&gt;", spamType);
    lawsuit =
        lawsuit.replace(
            "&lt;moreThanFiveLawsuits&gt;",
            sentMoreThanFiveLawsuits ? moreThanFiveLawsuits : lessThanFiveLawsuits);
    lawsuit = lawsuit.replace("&lt;undefined&gt;", "");
    lawsuit = lawsuit.replace("&lt;receivedSpamDate&gt;", DATE_FORMATTER.format(receivedSpamDate));
    lawsuit = lawsuit.replace("&lt;currentDate&gt;", DATE_FORMATTER.format(new Date()));

    // User fields
    lawsuit = lawsuit.replace("&lt;userFullName&gt;", userPrivateName + " " + userLastName);
    lawsuit = lawsuit.replace("&lt;userId&gt;", userID);
    lawsuit = lawsuit.replace("&lt;userAddress&gt;", userAddress);
    lawsuit = lawsuit.replace("&lt;userPhone&gt;", userPhone);
    lawsuit = lawsuit.replace("&lt;userFax&gt;", userFax);

    // First spam company
    lawsuit = lawsuit.replace("&lt;companyName&gt;", companyName);
    lawsuit = lawsuit.replace("&lt;companyId&gt;", companyId);
    lawsuit = lawsuit.replace("&lt;companyAddress&gt;", companyAddress);
    lawsuit = lawsuit.replace("&lt;companyPhone&gt;", companyPhone);
    lawsuit = lawsuit.replace("&lt;companyFax&gt;", companyFax);

    // Second spam company
    lawsuit = lawsuit.replace("&lt;company2Name&gt;", company2Name);
    lawsuit = lawsuit.replace("&lt;company2Id&gt;", company2Id);
    lawsuit = lawsuit.replace("&lt;company2Address&gt;", company2Address);
    lawsuit = lawsuit.replace("&lt;company2Phone&gt;", company2Phone);
    lawsuit = lawsuit.replace("&lt;company2Fax&gt;", company2Fax);

    return lawsuit;
  }

  private String getPdfFilename() {

    return DATE_TIME_FORMATTER.format(new Date()) + ".pdf";
  }

  private void initDirStructure() {

    lawsuitMainPath =
        Paths.get(
                Environment.getExternalStorageDirectory().getPath(),
                getString(R.string.app_name),
                LAWSUIT_MAIN_DIR_NAME)
            .toString();
    makeDir(lawsuitMainPath);

    lawsuitTemplateDirPath = Paths.get(lawsuitMainPath, LAWSUIT_TEMPLATE_DIR_NAME).toString();
    makeDir(lawsuitTemplateDirPath);

    try {
      lawsuitTemplateFilePath =
          Paths.get(lawsuitTemplateDirPath, LAWSUIT_TEMPLATE_FILE_NAME).toString();
    } catch (Exception e) {
      Toast.makeText(this, "No template file found for the lawsuit.", Toast.LENGTH_LONG).show();
    }

    // Output
    lawsuitOutputPath = Paths.get(lawsuitMainPath, LAWSUIT_OUTPUT_DIR_NAME).toString();
    makeDir(lawsuitOutputPath);
  }

  private void makeDir(String path) {

    File directory = new File(path);
    if (directory.exists() && directory.isDirectory()) directory.mkdirs();
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
        createPdf();
      }
      // If user denied permission before, show explanation dialog before requesting
      else {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

          new AlertDialog.Builder(this)
              .setTitle("Permission needed")
              .setMessage("Write permission needed for creating lawsuit PDF.")
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
                            "Write storage permission needed.",
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
      createPdf();
    }
  }

  // Call createPdf() if permission granted
  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == STORAGE_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        createPdf();
      } else {
        Toast.makeText(
                LawsuitPdfActivity.this, "Write storage permission needed.", Toast.LENGTH_LONG)
            .show();
      }
    }
  }
}

