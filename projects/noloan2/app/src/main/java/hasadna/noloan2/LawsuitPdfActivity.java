package hasadna.noloan2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.html2pdf.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import noloan.R;


public class LawsuitPdfActivity extends AppCompatActivity {
    // TODO: Add logs to class functions.
    private static final String TAG = "LawsuitPdfActivity";

    // Folder names
    private static final String LAWSUIT_MAIN_DIR_NAME = "lawsuits";
    private static final String LAWSUIT_TEMPLATE_DIR_NAME = "templates";
    private static final String LAWSUIT_OUTPUT_DIR_NAME = "pdf";
    private static final String LAWSUIT_TEMPLATE_FILE_NAME = "template.xhtml";

    // Folder paths - initDirStructure()
    private String lawsuitMainPath = "";
    private String lawsuitTemplateDirPath = "";
    private String lawsuitTemplateFilePath = "";
    private String lawsuitOutputPath = "";

    // Date formats
    private static final String DATE_TIME_FORMAT = "dd-M-yyyy hh-mm-ss";
    private static final String DATE_FORMAT = "dd-M-yyyy";
    private static final String TIME_ZONE = "Asia/Jerusalem";
    private SimpleDateFormat dateAndTimeFormatter;
    private SimpleDateFormat dateFormatter;

    // Initialise until App has client-side form
    private boolean sentHaser = true;
    private boolean sentMoreThanFiveLawsuits = false; // The past year
    private Date receivedSpamDate = new Date();

// ------------------------------- Lawsuit Form Fields ------------------------------- //
    // General
    private String spamType = "הודעה אלקטרונית";
    private String claimAmount = "סכום תביעה";
    private String moreThanFiveLawsuits = "הגיש";
    private String lessThanFiveLawsuits = "לא הגיש/ו";
    private String claimCaseHaser = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
    private String claimCaseSubscription = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";

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
// ---------------------------------------------------------------------------------------:
    Button createPdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawsuit_pdf);
        createPdfButton = (Button) findViewById(R.id.button_createPDF);

        getWriteStoragePermission();
        initDirStructure();

        dateAndTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateAndTimeFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        // Generate lawsuit onClick
        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });

    }

    private void createPdf() {

        // Activate iText license
        // TODO: Find a better solution for reading the licensekey file (This was a just a "quick fix" to get it working).
        InputStream licenseIs = getResources().openRawResource(R.raw.itextkey);
        LicenseKey.loadLicenseFile(licenseIs);
        try {
            HtmlConverter.convertToPdf(fillTemplate(), new FileOutputStream(Paths.get(lawsuitOutputPath, getPdfFilename()).toString()));
        } catch (IOException e) {
            Log.w(TAG, "Error creating PDF Lawsuit");
            e.printStackTrace();
        }
    }

    /**
     * Fill lawsuit template with user & spam company's details.
     *
     * @return Template's content as Html with fields replaced.
     */
    private String fillTemplate() {

        String lawsuit = readTemplate();

        // General form fields
        lawsuit = lawsuit.replace("&lt;claimCase&gt;", sentHaser ? claimCaseHaser : claimCaseSubscription);
        lawsuit = lawsuit.replace("&lt;claimAmount&gt;", claimAmount);
        lawsuit = lawsuit.replace("&lt;spamType&gt;", spamType);
        lawsuit = lawsuit.replace("&lt;moreThanFiveLawsuits&gt;", sentMoreThanFiveLawsuits ? moreThanFiveLawsuits : lessThanFiveLawsuits);
        lawsuit = lawsuit.replace("&lt;undefined&gt;", "");
        lawsuit = lawsuit.replace("&lt;receivedSpamDate&gt;", dateFormatter.format(receivedSpamDate));
        lawsuit = lawsuit.replace("&lt;currentDate&gt;", dateFormatter.format(new Date()));

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

    private String readTemplate() {

        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(lawsuitTemplateFilePath));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
            Log.w(TAG, "Error reading lawsuit's template.");
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private String getPdfFilename() {

        return dateAndTimeFormatter.format(new Date()) + ".pdf";
    }

    private void initDirStructure() {

        lawsuitMainPath = Paths.get(Environment.getExternalStorageDirectory().getPath(), getString(R.string.app_name), LAWSUIT_MAIN_DIR_NAME).toString();
        initDir(lawsuitMainPath);

        lawsuitTemplateDirPath = Paths.get(lawsuitMainPath, LAWSUIT_TEMPLATE_DIR_NAME).toString();
        initDir(lawsuitTemplateDirPath);

        // TODO: Currently template's file is been stored in user's device storage. Check to see if this should be part of the apk package.
        try {
            lawsuitTemplateFilePath = Paths.get(lawsuitTemplateDirPath, LAWSUIT_TEMPLATE_FILE_NAME).toString();
        } catch (Exception e) {
            Toast.makeText(this, "No template file found for the lawsuit.",
                    Toast.LENGTH_LONG).show();
        }

        // Output
        lawsuitOutputPath = Paths.get(lawsuitMainPath, LAWSUIT_OUTPUT_DIR_NAME).toString();
        initDir(lawsuitOutputPath);
    }

    /**
     * Create dir if it doesn't exists.
     * @param path Directory to look for.
     */
    private void initDir(String path) {

        File directory = new File(lawsuitTemplateDirPath);
        if (directory.exists() && directory.isDirectory())
            directory.mkdirs();
    }

    private void getWriteStoragePermission() {

        if (Build.VERSION.SDK_INT >= 23)
            if (!(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }
}
