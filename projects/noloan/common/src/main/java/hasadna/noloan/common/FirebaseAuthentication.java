package hasadna.noloan.common;

import android.util.Log;

import com.google.android.gms.tasks.TaskCompletionSource;
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

  public FirebaseAuthentication() {
    auth = FirebaseAuth.getInstance();
    user = auth.getCurrentUser();
  }

  public void signinAnonymusly() {
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

  public TaskCompletionSource<Boolean> signinAdmin(String email, String password) {
    Log.d(TAG, "signinAdmin starting");
    TaskCompletionSource result = new TaskCompletionSource<>();

    // If not already signed in.
    if (user == null) {
      auth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(
              task -> {
                if (task.isSuccessful()) {
                  Log.d(TAG, "Login admin successfully");
                  user = auth.getCurrentUser();
                  result.trySetResult(true);
                } else {
                  Log.e(TAG, "Failed to signin admin" + email + "  " + password);
                  result.trySetResult(false);
                }
              });
    } else {
      result.trySetResult(true);
    }
    return result;
  }

  public void signout() {
    auth.signOut();
    user = null;
  }

  public boolean isSignin() {
    return user != null;
  }

  public String getCurrentUserId() {
    if (user == null) {
      return null;
    }
    return user.getUid();
  }
}

