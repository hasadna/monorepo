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

public class LawsuitPdfActivity extends AppCompatActivity {
    // TODO: Add logs to class functions.
    private static final String TAG = "LawsuitPdfActivity";

    // Folder names configuration:
    // Name of general lawsuit dir.
    private static final String LAWSUIT_MAIN_DIR_NAME = "lawsuits";
    // Name of lawsuit template's dir.
    private static final String LAWSUIT_TEMPLATE_DIR_NAME = "templates";
    // Name of resulted lawsuit PDFs output dir
    private static final String LAWSUIT_OUTPUT_DIR_NAME = "pdf";
    // Name of lawsuit's template file.
    private static final String LAWSUIT_TEMPLATE_FILE_NAME = "template.xhtml";

    // Folder paths (See: 'initDirStructure()' for structure between folders)
    private String lawsuitMainPath = "";
    private String lawsuitTemplateDirPath = "";
    private String lawsuitTemplateFilePath = "";
    private String lawsuitOutputPath = "";

    // Date & Time formats: (Used for files names)
    // TODO: Check if this should be as part of a "File manager" class.
    private static final String DATE_TIME_FORMAT = "dd-M-yyyy hh-mm-ss";
    private static final String DATE_FORMAT = "dd-M-yyyy";
    private static final String TIME_ZONE = "Asia/Jerusalem";
    private SimpleDateFormat dateAndTimeFormatter;
    private SimpleDateFormat dateFormatter;

    // Template's inner text (been read on create from template's file):
    private String templateContent = "";

// ------------------------------- Lawsuit Form Fields ------------------------------- //
    // Type: SMS/Phone call/Email.
    private String spamType = "הודעה אלקטרונית";
    private String claimAmount = "סכום תביעה";
    // Date received the spam.
    private Date receivedSpamDate;
    // Current date of lawsuit creation (used for lawsuit signature).
    private Date currentDate;
    // Indicates whether user has sent 'haser'.
    private boolean sentHaser = true;
    // Indicates whether user has sent more then 5 lawsuits already in the past year.
    private boolean sentMoreThanFiveLawsuits = false;
    private String moreThanFiveLawsuits ="הגיש";
    private String lessThanFiveLawsuits = "לא הגיש/ו";
    private String fiveLawsuitsStatus =""; // Init onCreate().
    private String claimCaseHaser = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
    private String claimCaseSubscription = "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";
    // Cases: Didn't subscribe / Sent 'haser' but got spam.
    private String claimCase = ""; //  Init on fillInTemplate().

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
    Button createPdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawsuit_pdf);
        createPdfButton = (Button) findViewById(R.id.button_createPDF);

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
        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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
    protected void createPdf() throws IOException {

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
        String lawsuitOutputPdfFilePath = Paths.get(lawsuitOutputPath,createPdfLawsuitFileName()).toString();
        HtmlConverter.convertToPdf(templateContent, new FileOutputStream(lawsuitOutputPdfFilePath));
    }

    /**
     * Reads Html template's content to String. (Template's path stored at 'templatePath' class variable)
     * @return The String holding lawsuit's template's content in an Html format.
     */
    protected String readTemplate() {

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
    protected String fillInTemplate() {

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
     * Responsible for the output lawsuit PDF file name format.
     * @return File name with ".pdf" extension.
     */
    protected String createPdfLawsuitFileName(){

        String fileName = dateAndTimeFormatter.format(new Date()) + ".pdf";
        return fileName;
    }

    /**
     * This is where configurations of folders structure of Lawsuit Activity is been done.
     * In case where directories don't exists, it creates them.
     */
    protected void initDirStructure(){

        // Main LawsuitActivity path: 'app_name/LAWSUIT_MAIN_DIR_NAME/'
        lawsuitMainPath = Paths.get(Environment.getExternalStorageDirectory().getPath(),getString(R.string.app_name),LAWSUIT_MAIN_DIR_NAME).toString();
        if (!dirExists(lawsuitMainPath)){
            File directory = new File(lawsuitMainPath);
            directory.mkdirs();
        }

        // Templates dir path: 'lawsuitMainPath/LAWSUIT_TEMPLATE_DIR_NAME/'
        // TODO: Currently template's file is been stored in user's device storage. Check to see if this should be part of the apk package.
        lawsuitTemplateDirPath = Paths.get(lawsuitMainPath,LAWSUIT_TEMPLATE_DIR_NAME).toString();
        if (!dirExists(lawsuitTemplateDirPath)){
            File directory = new File(lawsuitTemplateDirPath);
            directory.mkdirs();
        }

        // Template file's path: 'lawsuitTemplateDirPath/LAWSUIT_TEMPLATE_FILE_NAME/'
        try {
            lawsuitTemplateFilePath = Paths.get(lawsuitTemplateDirPath, LAWSUIT_TEMPLATE_FILE_NAME).toString();
        }
        catch (Exception e){
            Toast.makeText(this, "No template file found for the lawsuit.",
                    Toast.LENGTH_LONG).show();
        }

        // Generated PDFs path:  'lawsuitMainPath/LAWSUIT_OUTPUT_DIR_NAME'
        lawsuitOutputPath = Paths.get(lawsuitMainPath,LAWSUIT_OUTPUT_DIR_NAME).toString();
        if (!dirExists(lawsuitOutputPath)){
            File directory = new File(lawsuitOutputPath);
            directory.mkdirs();
        }
    }

    /**
     * Checks if a directory given in the parameter exists.
     * @param path Directory to look for.
     * @return True - Exists, False - Doesn't exists.
     */
    protected boolean dirExists(String path)
    {
        boolean res = false;
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory())
            res = true;
        return res;
    }

    // Check external storage write permission:
    // TODO: Check if this should be done at an earlier activity.
    private boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

}
