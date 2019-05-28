package hasadna.noloan2;

import android.Manifest;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import hasadna.noloan2.protobuf.CourtProto.Court;
import hasadna.noloan2.protobuf.SmsProto.SmsMessage;
import hasadna.noloan2.protobuf.LawsuitProto.Lawsuit;

import noloan.R;

public class LawsuitActivity extends AppCompatActivity {

  private static final String TAG = "LawsuitFormFragment";
  private final int STORAGE_PERMISSION_CODE = 1;
  private boolean permissionGranted = false;

  Lawsuit lawsuitProto;
  SmsMessage selectedSmsSpam;
  Court selectedCourt;
  String absFilename;

  ArrayList<Court> courtList = null;
  NonSwipeableViewPager viewPager;
  AlertDialog.Builder courtBuilder;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lawsuit);

    // Hold data of current lawsuit, set lawsuitProto values from form fields in FormFragment
    lawsuitProto = Lawsuit.newBuilder().buildPartial();
    selectedSmsSpam =
        SmsMessage.newBuilder()
            .setSender(getIntent().getExtras().getString("from"))
            .setBody(getIntent().getExtras().getString("body"))
            .setReceivedAt(getIntent().getExtras().getString("receivedAt"))
            .build();

    // Used for dates in the lawsuit form
    DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
    DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

    courtList = initCourts();
    courtBuilder = new AlertDialog.Builder(LawsuitActivity.this);

    viewPager = findViewById(R.id.pager);
    PagerAdapter pagerAdapter = new FragmentPageAdapter(getSupportFragmentManager());
    viewPager.setAdapter(pagerAdapter);

    checkPermissions();
  }

  // Save PDF to device's external storage
  void createPdf() {
    if (permissionGranted) {
      String template = null;
      try {
        template = fillTemplate();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Add SMS to attachments page
      template +=
          String.format(getString(R.string.list_item_from), selectedSmsSpam.getSender()) + "\n";
      template +=
          String.format(getString(R.string.list_item_date), selectedSmsSpam.getReceivedAt()) + "\n";
      template +=
          String.format(getString(R.string.list_item_body), selectedSmsSpam.getBody()) + "\n";

      PdfDocument document = new PdfDocument();

      // Create 3 pages for the PDF (Size A4)
      PdfDocument.PageInfo firstPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
      PdfDocument.PageInfo secondPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 2).create();
      PdfDocument.PageInfo thirdPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 3).create();

      PdfDocument.Page firstPage = document.startPage(firstPageInfo);
      PdfDocument.Page secondPage = null;
      PdfDocument.Page thirdPage = null;
      Canvas canvas = firstPage.getCanvas();
      Paint paint = new Paint();
      int rowCounter = 0;

      int xPainter = 0;
      int yPainter = 0;

      // Draw lawsuit to PDF
      for (String line : template.split("\n")) {
        // Switch to 2nd page on row 56
        if (rowCounter == 56) {
          document.finishPage(firstPage);
          secondPage = document.startPage(secondPageInfo);
          canvas = secondPage.getCanvas();
          yPainter = 0;
        }
        // Switch to 3rd attachments page
        if (rowCounter == 77) {
          document.finishPage(secondPage);
          thirdPage = document.startPage(thirdPageInfo);
          canvas = thirdPage.getCanvas();
          yPainter = 0;
        }
        // Draw text as RTL, 20 is for right padding
        xPainter = (firstPageInfo.getPageWidth() - (int) paint.measureText(line)) - 20;
        canvas.drawText(line, xPainter, yPainter, paint);
        yPainter += paint.descent() - paint.ascent();
        rowCounter++;
      }
      document.finishPage(thirdPage);

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
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
      }

      document.close();
      this.absFilename = absFilename;
    }
  }

  // Read values from LawsuitProto
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

    // User
    lawsuit =
        lawsuit.replace(
            "<userFullName>", lawsuitProto.getPrivateName() + " " + lawsuitProto.getLastName());
    lawsuit = lawsuit.replace("<userId>", lawsuitProto.getUserId());
    lawsuit = lawsuit.replace("<userAddress>", lawsuitProto.getUserAddress());
    lawsuit = lawsuit.replace("<userPhone>", lawsuitProto.getUserPhone());
    lawsuit = lawsuit.replace("<userFax>", lawsuitProto.getUserFax());

    // Company
    lawsuit = lawsuit.replace("<companyName>", lawsuitProto.getCompanyName());
    lawsuit = lawsuit.replace("<companyId>", lawsuitProto.getCompanyId());
    lawsuit = lawsuit.replace("<companyAddress>", lawsuitProto.getCompanyAddress());
    lawsuit = lawsuit.replace("<companyPhone>", lawsuitProto.getCompanyPhone());
    lawsuit = lawsuit.replace("<companyFax>", lawsuitProto.getCompanyFax());

    // General
    lawsuit =
        lawsuit.replace(
            "<claimCase>", lawsuitProto.getSentHaser() ? claimCaseHaser : claimCaseSubscription);
    lawsuit = lawsuit.replace("<claimAmount>", lawsuitProto.getClaimAmount());
    lawsuit =
        lawsuit.replace(
            "<moreThanFiveLawsuits>",
            lawsuitProto.getMoreThanFiveClaims() ? moreThanFiveLawsuits : lessThanFiveLawsuits);
    lawsuit = lawsuit.replace("<receivedSpamDate>", selectedSmsSpam.getReceivedAt());
    lawsuit = lawsuit.replace("<currentDate>", DATE_FORMATTER.format(new Date()));
    lawsuit = lawsuit.replace("<undefined>", "");
    return lawsuit;
  }

  // Create a folder for generated PDFs (external storage)
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

  // Flags 'permissionGranted" for results
  private void checkPermissions() {
    // Ask runtime permissions for devices running SDK > 22
    if (Build.VERSION.SDK_INT >= 23) {

      // Permission granted
      if ((ActivityCompat.checkSelfPermission(
              LawsuitActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
          == PackageManager.PERMISSION_GRANTED)) {
        permissionGranted = true;
        createOutputDir();
      }
      // User denied permission before, show explanation dialog before requesting
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
                          this,
                          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                          STORAGE_PERMISSION_CODE))
              .setNegativeButton(
                  "cancel",
                  (dialog, which) -> {
                    Toast.makeText(this, "Permission is needed to create PDF.", Toast.LENGTH_LONG)
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

  // Pops a share dialog
  void sharePdf() {
    if (new File(absFilename).exists()) {
      Uri uri =
          FileProvider.getUriForFile(
              this, getPackageName() + ".fileprovider", new File(absFilename));
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.setType("application/pdf");
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      startActivity(Intent.createChooser(shareIntent, "Share via"));
    } else {
      Log.w(TAG, "Could not share pdf, file doesn't exists.\nabsFilename: " + absFilename);
    }
  }
  // Choose court from 2 fragments: FormFragment / ClaimFragment
  void displayCourtPicker(Fragment fragment) {
    String[] courtNames = new String[28];

    int index = 0;
    for (Court court : courtList) {
      courtNames[index++] = court.getName();
    }
    courtBuilder.setTitle("בחירת בית משפט");
    courtBuilder.setItems(
        courtNames,
        (dialog, which) -> {
          selectedCourt = courtList.get(which);

          // FormFragment / ClaimFragment
          if (LawsuitClaimFragment.class.isInstance(fragment)) {
            ((TextView) (findViewById(R.id.textView_ClaimFragment_courtName)))
                .setText("בית המשפט לתביעות קטנות - " + selectedCourt.getName());
            ((TextView) (findViewById(R.id.textView_ClaimFragment_courtAddress)))
                .setText(selectedCourt.getAddress());
            ((TextView) (findViewById(R.id.textView_ClaimFragment_courtFax)))
                .setText(selectedCourt.getFax());
          } else {
            ((TextView) findViewById(R.id.textView_selectCourt_FormFragment))
                .setText("בית המשפט לתביעות קטנות - " + selectedCourt.getName());
          }
          selectedCourt = courtList.get(which);
        });
    courtBuilder.show();
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == STORAGE_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        permissionGranted = true;
        createOutputDir();
      } else {
        Toast.makeText(this, "Permission is needed to create PDF.", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (viewPager.getCurrentItem() == 0) {
      // If the user is currently looking at the first page, allow the system to handle the
      // Back button_blue. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    } else {
      // Otherwise, select the previous step.
      viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }
  }

  public ArrayList<Court> initCourts() {
    ArrayList<Court> courts = new ArrayList<>(28);
    String[] courtList = getResources().getStringArray(R.array.courts_detailed);
    String[] courtDetail;

    for (int i = 0; i < courtList.length; i++) {
      courtDetail = courtList[i].split("\\|");
      Court court =
          Court.newBuilder()
              .setName(courtDetail[0])
              .setAddress(courtDetail[1])
              .setFax(courtDetail[2])
              .setWebsite(courtDetail[3])
              .build();
      courts.add(court);
    }
    return courts;
  }
}

