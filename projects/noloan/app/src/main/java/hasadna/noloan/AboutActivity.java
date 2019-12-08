package hasadna.noloan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import noloan.R;

public class AboutActivity extends AppCompatActivity {

  private static final String TAG = "AboutActivity";

  private TextView email;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.about_activity);

    Toolbar toolbar = findViewById(R.id.toolbar);
    TextView toolbarTitle = findViewById(R.id.toolbar_title);

    setSupportActionBar(toolbar);
    toolbarTitle.setText(toolbar.getTitle());

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(true);

    email = findViewById(R.id.textView_aboutPage_contactEmail);
    email.setText(R.string.app_emailAddress);
    email.setOnClickListener(
        v -> {
          Intent emailIntent =
              new Intent(
                  Intent.ACTION_SENDTO, Uri.fromParts("mailto", "noloan.app@gmail.com", null));
          startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
  }

  public static void startActivity(Context context) {
    Intent intent = new Intent(context, AboutActivity.class);
    context.startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

