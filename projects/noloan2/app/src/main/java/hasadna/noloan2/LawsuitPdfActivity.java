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
import com.itextpdf.text.DocumentException;
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

public class LawsuitPdfActivity extends AppCompatActivity { // TODO: Add logs to class functions.

    private static final String TAG = "LawsuitPdfActivity";

    // Folder names configurations:
    private static final String LAWSUIT_MAIN_DIR_NAME = "Lawsuits";                  // Name of general lawsuit dir.
    private static final String LAWSUIT_TEMPLATE_DIR_NAME = "Templates";             // Name of lawsuit template's dir.
    private static final String LAWSUIT_OUTPUT_DIR_NAME = "Lawsuits PDFs";           // Name of resulted lawsuit PDFs output dir.
    private static final String LAWSUIT_TEMPLATE_FILE_NAME = "LawsuitTemplate.xhtml";// Name of lawsuit's template file.
    private static String lawsuitOutputPdfFileName = "";                             // Init when user confirms form.

    // Folder paths (See: 'initDirStructure()' for structure between folders)
    private static String lawsuitMainPath = "";
    private static String lawsuitTemplateDirPath = "";
    private static String lawsuitTemplateFilePath = "";
    private static String lawsuitOutputPath = "";
    private static String lawsuitOutputPdfFilePath = "";                             // Init when user confirms form.

    // Date & Time formats: (Used for files names)
    // TODO: Check if this should be as part of a "File manager" class.
    private static final String DATE_TIME_FORMAT = "dd-M-yyyy hh-mm-ss";
    private static final String DATE_FORMAT = "dd-M-yyyy";
    private static final String TIME_ZONE = "Asia/Jerusalem";
    private static SimpleDateFormat dateAndTimeFormatter;
    private static SimpleDateFormat dateFormatter;

    // Template's inner text (been read on create from template's file):
    private static String templateContent = "";

// ------------------------------- Lawsuit Form Fields ------------------------------- //
    private String spamType = "הודעה אלקטרונית";        // Type: SMS/Phone call/Email.
    private String claimAmount = "סכום תביעה";
    private Date receivedSpamDate;                    // Date received the spam.
    private Date currentDate;                         // Current date of lawsuit creation (used for lawsuit signature).
    private boolean sentHaser = true;                 // Indicates whether user has sent.
    private boolean sentMoreThanFiveLawsuits = false; // Indicates whether user has sent more then 5 lawsuits already in the past year.
    private String moreThanFiveLawsuits ="הגיש";
    private String lessThanFiveLawsuits = "לא הגיש/ו";
    private String fiveLawsuitsStatus ="";            // Init onCreate().
    private String claimCaseHaser = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
    private String claimCaseSubscription = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
    private String claimCase = "";                    // "Didn't subscribe" / "Sent 'haser' but got spam". Set on fillInTemplate().

    // User:
    private String userPrivateName = "שם פרטי";
    private String userLastname = "שם משפחה";
    private String userID = "ת ז משתמש";
    private String userAddress = "כתובת משתמש";
    private String userPhone = "טלפון משתמש";
    private String userFax = "פקס משתמש";

    // First spam company's details:
    private String companyName = "שם החברה";
    private String companyId = "מספר ח.פ";
    private String companyAddress = "כתובת החברה";
    private String companyPhone = "טלפון החברה";
    private String companyFax = "פקס חברה";

    // Second spam company's det ails:
    private String company2Name = "שם החברה 2";
    private String company2Id = "מספר ח.פ 2";
    private String company2Address = "כתובת החברה 2";
    private String company2Phone = "טלפון החברה 2";
    private String company2Fax = "פקס חברה 2";
// ----------------------------- - -------------------------------------------------------:
    Button button_create_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawsuit_pdf);
        button_create_pdf = (Button) findViewById(R.id.button_createPDF);

        // Check Read/Write permissions:
        isWriteStoragePermissionGranted();

        // Initialize folders needed for activity:
        initDirStructure();

        // Read lawsuit template's content:
        templateContent = readTemplate();

        // Init dates:
        receivedSpamDate = new Date();
        currentDate = new Date();
        // Update date values:
        dateAndTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateAndTimeFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        // Generate lawsuit PDF OnClick:
        button_create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfLawsuitFileName();
                    createPdf();
                } catch (IOException e) {
                    Log.w(TAG, "Error creating PDF Lawsuit");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates a PDF of the lawusuit with all spam company & user's details.
     * @throws IOException  Thrown when there's an error reading lawusuit template file.
     * @throws DocumentException
     */
    public void createPdf() throws IOException {

        // Activate license:
        // TODO: Find a better solution for reading the licensekey file (This was a just a "quick fix" to get it working).
        InputStream licenseIs = getResources().openRawResource(R.raw.itextkey);
        LicenseKey.loadLicenseFile(licenseIs);

        // Update date values:
        dateAndTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        dateAndTimeFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        // Convert Html to PDF:

        templateContent = fillInTemplate();
        HtmlConverter.convertToPdf(templateContent, new FileOutputStream(lawsuitOutputPdfFilePath));
    }

    /**
     * Reads Html template's content to String. (Template's path stored at 'templatePath' class variable)
     * @return The String holding lawsuit's template's content in an Html format.
     */
    public String readTemplate() {

        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(lawsuitTemplateFilePath));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        String content = contentBuilder.toString();
        return content;
    }

    /**
     * Fill lawsuit template with user & spam company's details.
     * @return Template's content as Html, with fields replaced.
     */
    public String fillInTemplate() {

        // Read template's content:
        String res = templateContent;

        // Fill in general form details:
        claimCase = sentHaser ? claimCaseHaser: claimCaseSubscription;
        fiveLawsuitsStatus = sentMoreThanFiveLawsuits ? moreThanFiveLawsuits: lessThanFiveLawsuits;
        res = res.replace("&lt;claimCase&gt;", claimCase);
        res = res.replace("&lt;claimAmount&gt;", claimAmount);
        res = res.replace("&lt;spamType&gt;", spamType);
        res = res.replace("&lt;moreThanFiveLawsuits&gt;", fiveLawsuitsStatus);
        res = res.replace("&lt;undefined&gt;", "");
        res = res.replace("&lt;receivedSpamDate&gt;", dateFormatter.format(receivedSpamDate));
        res = res.replace("&lt;currentDate&gt;", dateFormatter.format(currentDate));

        // User details:
        res = res.replace("&lt;userFullName&gt;", userPrivateName + " " + userLastname);
        res = res.replace("&lt;userId&gt;", userID);
        res = res.replace("&lt;userAddress&gt;", userAddress);
        res = res.replace("&lt;userPhone&gt;", userPhone);
        res = res.replace("&lt;userFax&gt;", userFax);

        // First company's details:
        res = res.replace("&lt;companyName&gt;", companyName);
        res = res.replace("&lt;companyId&gt;", companyId);
        res = res.replace("&lt;companyAddress&gt;", companyAddress);
        res = res.replace("&lt;companyPhone&gt;", companyPhone);
        res = res.replace("&lt;companyFax&gt;", companyFax);

        // Second company's details:
        res = res.replace("&lt;company2Name&gt;", company2Name);
        res = res.replace("&lt;company2Id&gt;", company2Id);
        res = res.replace("&lt;company2Address&gt;", company2Address);
        res = res.replace("&lt;company2Phone&gt;", company2Phone);
        res = res.replace("&lt;company2Fax&gt;", company2Fax);

        return res;
    }

    /**
     * Genereates the lawsuit Pdf file name according to Time & Date user had confirmed the form.
     */
    public void createPdfLawsuitFileName(){

        lawsuitOutputPdfFileName = dateAndTimeFormatter.format(new Date()) + ".pdf";
        lawsuitOutputPdfFilePath = Paths.get(lawsuitOutputPath,lawsuitOutputPdfFileName).toString();
    }

    /**
     * This is where configurations of folders structure of Lawsuit Acitivity is been done.
     * In case where directories don't exists, it creates them.
     */
    public void initDirStructure(){

        // Main LawsuitActivity path:
        // app_name/lawsuit_main_dir/
        lawsuitMainPath = Paths.get(Environment.getExternalStorageDirectory().getPath(),getString(R.string.app_name),LAWSUIT_MAIN_DIR_NAME).toString();
        if (!dirExists(lawsuitMainPath)){
            File directory = new File(lawsuitMainPath);
            directory.mkdirs();
        }

        // Template's dir:
        // app_name/lawsuit_main_dir/lawsuit_template_dir/
        // TODO: Currently template's file is been stored in user's device storage. Check to see if this should be part of the apk package.
        lawsuitTemplateDirPath = Paths.get(Environment.getExternalStorageDirectory().getPath(),getString(R.string.app_name),LAWSUIT_MAIN_DIR_NAME,LAWSUIT_TEMPLATE_DIR_NAME).toString();
        if (!dirExists(lawsuitTemplateDirPath)){
            File directory = new File(lawsuitTemplateDirPath);
            directory.mkdirs();
        }

        // Template file's path:
        try {
            lawsuitTemplateFilePath = Paths.get(lawsuitTemplateDirPath, LAWSUIT_TEMPLATE_FILE_NAME).toString();
        }
        catch (Exception e){
            Toast.makeText(this, "No template file found for the lawsuit",
                    Toast.LENGTH_LONG).show();
        }
        // User's Lawsuit dir:
        // app_name/lawsuit_output_dir/
        lawsuitOutputPath = Paths.get(Environment.getExternalStorageDirectory().getPath(),getString(R.string.app_name)+" - "+ LAWSUIT_OUTPUT_DIR_NAME).toString();
        if (!dirExists(lawsuitOutputPath)){
            File directory = new File(lawsuitOutputPath);
            directory.mkdirs();
        }
    }

    /**
     * Cehcks if a directory given in the parameter exists.
     * @param path Directory to look for.
     * @return True - Exists, False - Doesn't exists.
     */
    public boolean dirExists(String path)
    {
        boolean res = false;
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory())
            res = true;
        return res;
    }

    // Check permissions: TODO: Check if this should be done at an earlier activity.
    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

}
