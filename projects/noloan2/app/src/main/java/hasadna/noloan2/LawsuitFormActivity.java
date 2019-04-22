package hasadna.noloan2;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
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
import android.graphics.pdf.PdfDocument.PageInfo;
import android.graphics.pdf.PdfDocument.Page;
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

public class LawsuitFormActivity extends AppCompatActivity {
  // TODO: Add logs.
  private static final String TAG = "LawsuitFormActivity";
  private final int STORAGE_PERMISSION_CODE = 1;
  private boolean permissionGranted = false;

  // Formats
  static final String TIME_ZONE = "Asia/Jerusalem";
  static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd-M-yyyy hh-mm-ss");
  static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-M-yyyy");

  // Lawsuit optional fields
  private String moreThanFiveLawsuits = "הגיש";
  private String lessThanFiveLawsuits = "לא הגיש/ו";
  private String claimCaseHaser =
      "לאחר שהתובע חזר בו מהסכמתו לקבלת דבר/י פרסומת\nמהנתבע/ים, על-ידי משלוח הודעת סירוב כהגדרתה בחוק";
  private String claimCaseSubscription =
      "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש  \nלקבלת דבר/י הפרסומת";

  EditText receivedDate;
  DatePickerDialog datePickerDialog;
  Calendar calendar;
  Button createPdfButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lawsuit_pdf);

    createPdfButton = findViewById(R.id.button_createPDF);
    receivedDate = findViewById(R.id.editText_spamDate);

    setTimeZones();
    createPdfButton.setOnClickListener(
        v -> {
          checkPermissions();
          if (permissionGranted) {
            sharePdf(createPdf());
          }
        });

    receivedDate.setText(getIntent().getExtras().getString("receivedAt"));
    receivedDate.setOnClickListener(v -> displayDatePicker());
  }

  // Used for date fields in the lawsuit
  private void setTimeZones() {
    DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
    DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
  }

  // Save PDF to device. Return absFilename of the file
  private String createPdf() {

    // Read and fill template
    String template = null;
    try {
      template = fillTemplate();
    } catch (IOException e) {
      e.printStackTrace();
    }

    PdfDocument document = new PdfDocument();

    // Create 2 pages for the PDF (Size A4)
    PageInfo firstPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
    PageInfo secondPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
    Page firstPage = document.startPage(firstPageInfo);
    Page secondPage = null;
    Canvas canvas = firstPage.getCanvas();
    Paint paint = new Paint();
    int rowCounter = 0;

    int xPainter = 0;
    int yPainter = 0;

    // Draw lines to PDF
    for (String line : template.split("\n")) {
      // Switch to 2nd page on row 56
      if (rowCounter == 56) {
        document.finishPage(firstPage);
        secondPage = document.startPage(secondPageInfo);
        canvas = secondPage.getCanvas();
        yPainter = 0;
      }
      // Draw text as RTL, 20 is for right padding
      xPainter = (firstPageInfo.getPageWidth() - (int) paint.measureText(line)) - 20;
      canvas.drawText(line, xPainter, yPainter, paint);
      yPainter += paint.descent() - paint.ascent();
      rowCounter++;
    }
    document.finishPage(secondPage);

    // Save file to external storage
    String absFilename =
        Paths.get(
                Environment.getExternalStorageDirectory().getPath(),
                getString(R.string.app_name),
                getString(R.string.output_folder_name),
                (DATE_TIME_FORMATTER.format(new Date()) + ".pdf"))
            .toString();
    try {
      document.writeTo(new FileOutputStream(new File(absFilename)));
      Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
    }

    document.close();
    return absFilename;
  }

  // Fill the template with the user's and the company's details
  private String fillTemplate() throws IOException {

    String lawsuit = null;
    try {
      InputStream inputStream = getAssets().open("template.txt");
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
            "<claimCase>",
            ((CheckBox) findViewById(R.id.checkBox_sentHaser)).isChecked()
                ? claimCaseHaser
                : claimCaseSubscription);
    lawsuit =
        lawsuit.replace(
            "<claimAmount>", ((EditText) findViewById(R.id.claimAmount)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<moreThanFiveLawsuits>",
            ((CheckBox) findViewById(R.id.checkbox_fiveLawsuits)).isChecked()
                ? moreThanFiveLawsuits
                : lessThanFiveLawsuits);
    lawsuit = lawsuit.replace("<undefined>", "");
    lawsuit = lawsuit.replace("<receivedSpamDate>", receivedDate.getText().toString());
    lawsuit = lawsuit.replace("<currentDate>", DATE_FORMATTER.format(new Date()));

    // User fields
    lawsuit =
        lawsuit.replace(
            "<userFullName>",
            ((EditText) findViewById(R.id.userPrivateName)).getText().toString()
                + " "
                + ((EditText) findViewById(R.id.userLastName)).getText().toString());
    lawsuit =
        lawsuit.replace("<userId>", ((EditText) findViewById(R.id.userID)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<userAddress>", ((EditText) findViewById(R.id.userAddress)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<userPhone>", ((EditText) findViewById(R.id.userPhone)).getText().toString());
    lawsuit =
        lawsuit.replace("<userFax>", ((EditText) findViewById(R.id.userFax)).getText().toString());

    // Spam company
    lawsuit =
        lawsuit.replace(
            "<companyName>", ((EditText) findViewById(R.id.companyName)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<companyId>", ((EditText) findViewById(R.id.companyId)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<companyAddress>",
            ((EditText) findViewById(R.id.companyAddress)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<companyPhone>", ((EditText) findViewById(R.id.companyPhone)).getText().toString());
    lawsuit =
        lawsuit.replace(
            "<companyFax>", ((EditText) findViewById(R.id.companyFax)).getText().toString());

    return lawsuit;
  }

  // Pops a dialog asking the user to share the pdf
  private void sharePdf(String absFilename) {
    if (new File(absFilename).exists()) {
      new AlertDialog.Builder(this, R.style.AlertDialog)
          .setTitle("כתב תביעה נוצר")
          .setMessage("כתב התביעה נוצר ונשמר במכשירך.\n האם תרצה/י לשתף?")
          .setPositiveButton(
              "כן",
              (dialog, which) -> {
                Uri uri =
                    FileProvider.getUriForFile(
                        getApplicationContext(),
                        getPackageName() + ".fileprovider",
                        new File(absFilename));
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
    Log.w(TAG, "Could not share pdf, file doesn't exists.\nabsFilename: " + absFilename);
  }

  // Pops a calendar dialog when clicking on date fields
  private void displayDatePicker() {
    calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);

    datePickerDialog =
        new /**/ DatePickerDialog(
            LawsuitFormActivity.this,
            (datePicker, currentYear, currentMonth, currentDay) ->
                receivedDate.setText(day + "/" + (month + 1) + "/" + year),
            year,
            month,
            day);
    datePickerDialog.show();
  }

  private void checkPermissions() {
    // Ask runtime permissions for devices running SDK > 22
    if (Build.VERSION.SDK_INT >= 23) {

      // Permission granted
      if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED)) {
        permissionGranted = true;
        createOutputDir();
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
                          LawsuitFormActivity.this,
                          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                          STORAGE_PERMISSION_CODE))
              .setNegativeButton(
                  "cancel",
                  (dialog, which) -> {
                    Toast.makeText(
                            LawsuitFormActivity.this,
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
      permissionGranted = true;
      createOutputDir();
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == STORAGE_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        permissionGranted = true;
        createOutputDir();
      } else {
        Toast.makeText(
                LawsuitFormActivity.this, "Permission is needed to create PDF.", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  // Create folder in external storage for saved PDFs
  private void createOutputDir() {
    File directory =
        new File(
            Paths.get(
                    Environment.getExternalStorageDirectory().getPath(),
                    getString(R.string.app_name),
                    getString(R.string.output_folder_name))
                .toString());
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }
}

