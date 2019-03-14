package hasadna.noloan2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import noloan.R;

// Based on http://pcessflight.com/smart-android-splash-screen-grabbing-permissions/
public class SplashPermissionActivity extends AppCompatActivity {

  private static final int PERMISSION_REQUEST_CODE = 123;
  final String[] requiredPermissions = new String[] {Manifest.permission.READ_SMS};

  static final long SPLASH_TIME_MS = 1000;

  long startTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_permission);

    startTime = System.currentTimeMillis();

    if (Build.VERSION.SDK_INT >= 23) {
      checkPermissions();
    } else {
      startNextActivity();
    }
  }

  private void checkPermissions() {
    String[] ungrantedPermissions = permissionsStillNeeded();
    if (ungrantedPermissions.length == 0) {
      startNextActivity();
    } else {
      Toast.makeText(this, "Permission for reading sms required", Toast.LENGTH_LONG).show();
      requestPermissions(ungrantedPermissions, PERMISSION_REQUEST_CODE);
    }
  }

  private String[] permissionsStillNeeded() {
    ArrayList<String> result = new ArrayList<>();
    for (String permission : requiredPermissions) {
      if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        result.add(permission);
      }
    }
    return result.toArray(new String[result.size()]);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      checkPermissions();
    }
  }

  private void startNextActivity() {
    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                startActivity(new Intent(SplashPermissionActivity.this, MainActivity.class));
                finish();
              }
            },
            SPLASH_TIME_MS);
  }
}

