package hasadna.noloan.common;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FirebaseAuthontication
{
  private static final String TAG = "FirebaseAuthontication";

  private static FirebaseAuthontication instance;
  private FirebaseUser user;
  private FirebaseAuth auth;

  public static FirebaseAuthontication getInstance()
  {
    if(instance == null)
    {
      instance = new FirebaseAuthontication();
    }
    return instance;
  }

  public void signinAnonymusly() {
    auth = FirebaseAuth.getInstance();
    auth.signInAnonymously().addOnCompleteListener(task ->
    {
      if (task.isSuccessful()) {
        user = auth.getCurrentUser();
      } else {
        Log.w(TAG, "signInAnonymously:failure", task.getException());
      }
    });
  }

  public boolean containCurrentUserId(List<String > ids)
  {
    return ids.contains(user.getUid());
  }

  public String getCurrentUserId()
  {
    return user.getUid();
  }
}
