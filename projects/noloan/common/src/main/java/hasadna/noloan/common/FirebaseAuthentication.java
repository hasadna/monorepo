package hasadna.noloan.common;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Singelton to deal with everything to do with authentication.
public class FirebaseAuthentication {
  private static final String TAG = "FirebaseAuthentication";

  private static FirebaseAuthentication instance;
  private FirebaseUser user;
  private FirebaseAuth auth;

  public static FirebaseAuthentication getInstance() {
    if (instance == null) {
      instance = new FirebaseAuthentication();
    }
    return instance;
  }

  public void signinAnonymusly() {
    auth = FirebaseAuth.getInstance();
    auth.signInAnonymously()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                user = auth.getCurrentUser();
              } else {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
              }
            });
  }


  public String getCurrentUserId() {
    return user.getUid();
  }
}

