package hasadna.noloan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import hasadna.noloan.firestore.FirestoreClient;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  private static final String TAG = "MainActivity";

  private DrawerLayout drawerLayout;

  private SpamRecyclerAdapter spamAdapter;
  private SmsRecyclerAdapter smsAdapter;
  private RecyclerView recycler;
  private boolean spamActive;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Toolbar
    AppBarLayout toolbarContent = findViewById(R.id.toolbar_content);
    Toolbar toolbar = toolbarContent.findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    TextView toolbarTitle = toolbarContent.findViewById(R.id.toolbar_title);
    toolbarTitle.setText(toolbar.getTitle());
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);

    // drawerLayout
    drawerLayout = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    // Navigation
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    // Reading Sms and spams
    List<SmsMessage> messages = readSmsFromDevice();
    List<SmsMessage> spam = SpamHolder.getInstance().getSpam();

    // Create a list of the intersection between the two lists, messages and spam
    // Based on https://www.baeldung.com/java-lists-intersection
    List<SmsMessage> results =
        messages.stream().distinct().filter(spam::contains).collect(Collectors.toList());

    // Filling the recycler
    recycler = findViewById(R.id.recycler_view);
    spamAdapter = new SpamRecyclerAdapter();
    smsAdapter = new SmsRecyclerAdapter(messages);
    spamActive = false;
    recycler.setAdapter(smsAdapter);
    recycler.setLayoutManager(new LinearLayoutManager(this));
    TextView statusTitle = findViewById(R.id.status_lawsuit);
    statusTitle.setText(
        (String.format(getResources().getString(R.string.content_summary), spam.size())));
  }

  // Reads SMS. If no permissions are granted, exit app.
  private ArrayList<SmsMessage> readSmsFromDevice() {
    // Check for permission reading sms
    int permissionStatus = checkSelfPermission(Manifest.permission.READ_SMS);

    // If don't have permission show toast and close the app
    if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
      Log.e("ReadSms", "No permission for reading SMSs");
      Toast.makeText(this, "Permission for reading sms required", Toast.LENGTH_LONG).show();
      finish();
      return null;
    }
    return getSmsList();
  }

  // Get a list of all SMS messages in the inbox.
  private ArrayList<SmsMessage> getSmsList() {
    ArrayList<SmsMessage> smsList = new ArrayList<>();
    Cursor cursor =
        getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

    if (cursor.moveToFirst()) {
      do {
        SmsMessage sms =
            SmsMessage.newBuilder()
                .setSender(cursor.getString(cursor.getColumnIndexOrThrow("address")))
                .setBody(cursor.getString(cursor.getColumnIndexOrThrow("body")))
                .setReceivedAt(
                    new SimpleDateFormat("dd/M/yyyy")
                        .format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date")))))
                .build();
        smsList.add(sms);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return smsList;
  }

  // Handle navigation view item clicks here.
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_about) {
      openAbout();
    } else if (id == R.id.nav_change) {
      changeAdapter();
      if (spamActive) {
        item.setTitle("הצג סמס");
      } else {
        item.setTitle("הצג ספאם");
      }
    }
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void openAbout() {
    AboutActivity.startActivity(this);
  }

  public void changeAdapter() {
    if (spamActive) {
      recycler.setAdapter(smsAdapter);
    } else {
      recycler.setAdapter(spamAdapter);
    }
    spamActive = !spamActive;
  }
}

