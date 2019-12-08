package hasadna.noloan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import noloan.R;

public class TermsOfUsageActivity extends AppCompatActivity {

  private static String TAG = "TermsOfUsageActivity";
  TextView email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terms_of_usage);

    Toolbar toolbar = findViewById(R.id.toolbar);
    TextView toolbarTitle = findViewById(R.id.toolbar_title);

    setSupportActionBar(toolbar);
    toolbarTitle.setText(toolbar.getTitle());

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(true);

    email = findViewById(R.id.textView_contact_email);
    email.setText(R.string.termsOfUsage_email);
    email.setOnClickListener(
        v -> {
          Intent emailIntent =
              new Intent(
                  Intent.ACTION_SENDTO,
                  Uri.fromParts("mailto", getString(R.string.termsOfUsage_email), null));
          startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
  }

  public static void startActivity(Context context) {
    Intent intent = new Intent(context, TermsOfUsageActivity.class);
    context.startActivity(intent);
  }
}

