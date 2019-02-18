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

import com.itextpdf.licensekey.LicenseKey;
import com.itextpdf.text.DocumentException;
import com.itextpdf.html2pdf.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import noloan.R;

public class LawsuitPdf extends AppCompatActivity { // TODO: Add logs to class functions.


    private static final String TAG = "Lawsuit";

    // Folder configurations:
    // TODO: Check if preferable to move these to an external folder configuration file.
    private static String appName = "";                                             // Get App's name as main dir.
    private static final String lawsuitPath = "/Lawsuits/";                         // General lawsuits dir.
    private static String templatePath = lawsuitPath + "/Templates/template.xhtml"; // Lawsuit Template's dir & template's path.
    private static final String lawsuitOutput = lawsuitPath;                        // Lawsuits output dir.

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
//  TODO: Check if preferable to move this to an external file holding constant String values of lawsuit form.
    private String spamType = "הודעה אלקטרונית";        // Type: SMS/Phone call/Email.
    private String claimAmount = "סכום תביעה";
    private Date receivedSpamDate;                    // Date received the spam.
    private Date currentDate;                         // Current date of lawsuit creation (used for lawsuit signature).
    private boolean sentHaser = true;                 // Indicates whether user has sent "haser" or not.
    private boolean sentMoreThanFiveLawsuits = false; // Indicates whether user has sent more then 5 lawsuits already in the past year or not.
    private String moreThanFiveLawsuits = sentMoreThanFiveLawsuits ? "הגיש" : "לא הגיש/ו";
    private String claimCase = sentHaser ? "לאחר שהתובע חזר בו מהסכמתו לקבלת דבר/י פרסומת מהנתבע/ים, על-ידי משלוח הודעת סירוב כהגדרתה בחוק" : "למרות שח\"מ לא נתן את הסכמתו המפורשת מראש לקבלת דבר/י הפרסומת";

    // User:
    private String userPrivateName = "שם פרטי";
    private String userLastname = "שם משפחה";
    private String userID = "תז משתמש";
    private String userAddress = "כתובת משתמש";
    private String userPhone = "טלפון משתמש";
    private String userFax = "פקס משתמש";

    // First spam company's details:
    private String companyName = "שם החברה";
    private String companyId = "מספר ח.פ";
    private String companyAddress = "כתובת החברה";
    private String companyPhone = "טלפון החברה";
    private String companyFax = "פקס חברה";

    // Second spam company's details:
    private String company2Name = "שם החברה 2";
    private String company2Id = "מספר ח.פ 2";
    private String company2Address = "כתובת החברה 2";
    private String company2Phone = "טלפון החברה 2";
    private String company2Fax = "פקס חברה 2";
// -------------------------------------------------------------------------------------:
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawsuit_pdf);
        btnCreate = (Button) findViewById(R.id.button_createPDF);
        appName = getString(R.string.app_name); // Get App's name

        // Check Read/Write permissions:
        isWriteStoragePermissionGranted();

        // Read lawsuit template's content:
        templatePath = Environment.getExternalStorageDirectory().getPath() + "/" + appName + "/" + templatePath;
        templateContent = readTemplate();

        // Init dates:
        receivedSpamDate = new Date();
        currentDate = new Date();

        // Generate lawsuit PDF OnClick:
        btnCreate.setOnClickListener(new View.OnClickListener() {
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

        // Create lawsuit's file name with current date:
        String directory_path = Environment.getExternalStorageDirectory().getPath() + lawsuitOutput;
        Date dt = new Date();
        String targetPdf = directory_path + dateAndTimeFormatter.format(dt) + ".pdf";

        // Convert Html to PDF:
        templateContent = fillInTemplate();
        HtmlConverter.convertToPdf(templateContent, new FileOutputStream(targetPdf));
    }

    /**
     * Reads Html template's content to String. (Template's path stored at 'templatePath' class variable)
     * @return The String holding lawsuit's template's content in an Html format.
     */
    public String readTemplate() {

        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(templatePath));
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
     *
     * @return Template's content as Html, with fields replaced.
     */
    public String fillInTemplate() {

        // Read template's content:
        String res = templateContent;

        // Fill in general form details:
        res = res.replace("&lt;claimCase&gt;", claimCase);
        res = res.replace("&lt;claimAmount&gt;", claimAmount);
        res = res.replace("&lt;spamType&gt;", spamType);
        res = res.replace("&lt;moreThanFiveLawsuits&gt;", moreThanFiveLawsuits);
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
