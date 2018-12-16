package projects.noloan.app;

import android.app.Application;

import org.json.JSONException;

import java.io.IOException;

import com.google.startupos.examples.android.FirestoreInitializer;

public class MainApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    try {
      FirestoreInitializer.init(getApplicationContext(), "google-services.json");
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }
}

