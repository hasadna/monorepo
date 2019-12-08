package hasadna.noloan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import hasadna.noloan.common.FirebaseAuthentication;
import hasadna.noloan.common.FirestoreClient;
import hasadna.noloan.mainactivity.MainActivity;
import noloan.R;

// Permission request Based on
// http://pcessflight.com/smart-android-splash-screen-grabbing-permissions/
public class SplashScreenActivity extends AppCompatActivity {

  static final long SPLASH_TIME_MS = 1000;
  private static final int PERMISSION_REQUEST_CODE = 123;
  final String[] requiredPermissions = new String[] {Manifest.permission.READ_SMS};
  private static final String TAG = "SplashScreenActivity";

  Handler handler;

  TaskCompletionSource<Boolean> permissionTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_permission);

    handler = new Handler(Looper.getMainLooper());

    permissionTask = new TaskCompletionSource<>();
    SplashTask task = new SplashTask();
    task.execute();
  }

  private void checkPermissions() {
    String[] ungrantedPermissions = permissionsStillNeeded();
    if (ungrantedPermissions.length == 0) {
      permissionTask.setResult(true);
    } else {
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
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        permissionTask.setResult(true);
      } else {
        permissionTask.setResult(false);
      }
    }
  }

  // Start the main Activity
  private void startNextActivity() {
    handler.postDelayed(
        () -> {
          startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
          finish();
        },
        SPLASH_TIME_MS);
  }

  // Main Task to get the permissions and smsMessages from the DB
  private class SplashTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
      checkPermissions();
      FirebaseAuthentication.getInstance().signInAnonymusly();
      Task messagesTask = new FirestoreClient().StartListeningToMessages().getTask();

      Task<Boolean> permissions = permissionTask.getTask();
      try {
        Tasks.await(permissions);
        Tasks.await(messagesTask);
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
      if (!permissions.getResult()) {
        handler.post(
            () ->
                Toast.makeText(
                        getApplicationContext(),
                        "This App require SMS reading To work",
                        Toast.LENGTH_SHORT)
                    .show());
        finishAndRemoveTask();
      } else {
        startNextActivity();
      }
      return null;
    }
  }
}

