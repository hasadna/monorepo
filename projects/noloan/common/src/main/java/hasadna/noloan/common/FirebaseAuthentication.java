package hasadna.noloan.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
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

  public FirebaseAuthentication()
  {
    auth = FirebaseAuth.getInstance();
    user = auth.getCurrentUser();

  }

  public void signinAnonymusly() {
    auth.signInAnonymously()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            user = auth.getCurrentUser();
          } else {
            Log.w(TAG, "signInAnonymously:failure", task.getException());
          }
        });
  }


  public TaskCompletionSource<Boolean> signinAdmin(String email, String password) {
    Log.d(TAG, "starting to log in!");
    TaskCompletionSource t = new TaskCompletionSource<>();

    //if not already signed in.
    if (user == null) {
      auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          Log.d(TAG, "login admin successfully");
          user = auth.getCurrentUser();
          t.trySetResult(true);
        } else {
          Log.e(TAG, "failed to signin admin" + email + "  " + password);
          t.trySetResult(false);
        }
      });
    }
    else
    {
      t.trySetResult(true);
    }
    return t;
  }

  // signout take time, we can listen to one of the event it fire.
  public void signout()
  {
    auth.signOut();
  }

  public boolean isSignin()
  {
    return user != null;
  }

  public String getCurrentUserId() {
    if(user == null)
    {
      return "no one";
    }
    return user.getUid();
  }
}

