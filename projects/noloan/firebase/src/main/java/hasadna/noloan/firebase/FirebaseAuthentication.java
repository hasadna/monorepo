package hasadna.noloan.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import hasadna.noloan.protobuf.SmsProto;

public class FirebaseAuthentication {
  private static final String TAG = "FirebaseAuthentication";

  private static FirebaseAuthentication instance;

  private FirebaseUser user;
  private FirebaseAuth firebaseAuth;

  public static FirebaseAuthentication getInstance() {
    if (instance == null) {
      instance = new FirebaseAuthentication();
    }
    return instance;
  }


  public void signinAnonymusly() {
    firebaseAuth = FirebaseAuth.getInstance();
    firebaseAuth.signInAnonymously().addOnCompleteListener(task ->
    {
      if (task.isSuccessful()) {
        user = firebaseAuth.getCurrentUser();
      } else {
        Log.w(TAG, "signInAnonymously:failure", task.getException());
      }
    });
  }

  public boolean isCurrentUser(String id) {
    return user.getUid().equals(id);
  }

  public boolean containCurrentUser(List<String> ids) {
    return ids.contains(user.getUid());
  }

  public String getCurrentUserId() {
    return user.getUid();
  }
}
