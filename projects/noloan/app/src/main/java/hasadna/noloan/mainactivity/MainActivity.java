package hasadna.noloan.mainactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hasadna.noloan.AboutActivity;
import hasadna.noloan.TermsOfUsageActivity;
import hasadna.noloan.common.SmsMessages;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import noloan.R;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        InboxFragment.OnFragmentInteractionListener,
        SpamFragment.OnFragmentInteractionListener {

  private static final String TAG = "MainActivity";

  SpamFragment spamFragment;
  private DrawerLayout drawerLayout;
  TabLayout tabLayout;
  TextView statusTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_activity);

    // Read inbox messages
    SmsMessages.get().setInboxMessages(readSmsFromDevice());

    // Toolbar
    AppBarLayout toolbarContent = findViewById(R.id.toolbar_content);
    Toolbar toolbar = toolbarContent.findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.toolbar_title_mainActivity_text);
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

    // Status title
    statusTitle = findViewById(R.id.textView_numberOfMessages);

    // ViewPager
    ViewPager viewPager = findViewById(R.id.viewPager);
    // RTL swiping (Along with recyclerView.setRotationY(180) in fragments)
    viewPager.setRotationY(180);
    MainActivityPagerAdapter pagerAdapter =
        new MainActivityPagerAdapter(getSupportFragmentManager());
    InboxFragment inboxFragment = new InboxFragment();
    spamFragment = new SpamFragment();
    pagerAdapter.addFragment(inboxFragment, getString(R.string.inboxFragment_title));
    pagerAdapter.addFragment(spamFragment, getString(R.string.spamFragment_title));
    viewPager.setAdapter(pagerAdapter);
    tabLayout = findViewById(R.id.TabLayout);
    tabLayout.setupWithViewPager(viewPager);

    updateTitles();
  }

  // Reads SMS. If no permissions are granted, exit app.
  ArrayList<SmsMessage> readSmsFromDevice() {
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
  ArrayList<SmsMessage> getSmsList() {
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
    } else if (id == R.id.nav_termOfUsage) {
      TermsOfUsageActivity.startActivity(this);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  // Update the number of messages in the title - Called from recyclerViewer adapters when list
  // changes
  public void updateTitles() {
    // Main title - Suspicious messages found in the inbox similar to the spams in the DB (body &
    // sender)
    // Not necessarily the number of messages the user had suggested.
    statusTitle.setText(String.valueOf(SmsMessages.get().countInboxSpam()));

    // Inbox tab title
    tabLayout
        .getTabAt(0)
        .setText(
            getString(R.string.inboxFragment_title, SmsMessages.get().getInboxMessages().size()));

    // Spam tab title
    tabLayout
        .getTabAt(1)
        .setText(
            getString(
                R.string.spamFragment_title,
                spamFragment != null ? spamFragment.getSpamCount() : 0));
  }

  private void openAbout() {
    AboutActivity.startActivity(this);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {}
}

