package hasadna.noloan.admin.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hasadna.noloan.common.FirebaseAuthentication;

public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    if (FirebaseAuthentication.getInstance().isSignin()) {
      startActivity(new Intent(LoginActivity.this, SplashScreenActivity.class));
      finish();
    } else {
      // for debugging
      TextView currentUser = findViewById(R.id.current_text);
      currentUser.setText(FirebaseAuthentication.getInstance().getCurrentUserId());

      TextView emailText = findViewById(R.id.email_Text);
      TextView passwordText = findViewById(R.id.password_text);

      Button login = findViewById(R.id.login_button);
      login.setOnClickListener(
          v -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
              FirebaseAuthentication.getInstance()
                  .signinAdmin(email, password)
                  .getTask()
                  .addOnCompleteListener(
                      task -> {
                        if (task.isSuccessful()) {
                          startActivity(new Intent(LoginActivity.this, SplashScreenActivity.class));
                          finish();
                          // currentUser.setText(FirebaseAuthentication.getInstance().getCurrentUserId());
                        } else {
                          Toast.makeText(getBaseContext(), "failed to login", Toast.LENGTH_LONG)
                              .show();
                        }
                      });
            } else {
              Toast.makeText(this, "email or password is empty", Toast.LENGTH_LONG).show();
            }
          });

      // fo debugging
      Button signout = findViewById(R.id.signout_button);
      signout.setOnClickListener(
          v -> {
            FirebaseAuthentication.getInstance().signout();
            currentUser.setText(FirebaseAuthentication.getInstance().getCurrentUserId());
          });
    }
  }
}

