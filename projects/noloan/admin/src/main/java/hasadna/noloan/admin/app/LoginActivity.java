package hasadna.noloan.admin.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hasadna.noloan.common.FirebaseAuthentication;

// TODO Read about FirebaseUI
public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // In case the admin already loged in
    if (FirebaseAuthentication.getInstance().isSignin()) {
      startActivity(new Intent(LoginActivity.this, SplashScreenActivity.class));
      finish();
    } else {
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
                        } else {
                          Toast.makeText(getBaseContext(), "Failed to login", Toast.LENGTH_LONG)
                              .show();
                        }
                      });
            } else {
              Toast.makeText(this, "Email or password is empty", Toast.LENGTH_LONG).show();
            }
          });
    }
  }
}

