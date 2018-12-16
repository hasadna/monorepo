package projects.noloan.app;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, MessagesAdapter.OnDataFetched {

  private static final String TAG = "MainActivity";

  private DrawerLayout drawerLayout;
  private RecyclerView recyclerView;
  private MessagesAdapter adapter;
  private RecyclerView.LayoutManager layoutManager;
  TextView summaryTextView;
  CardView summaryCard;
  LinearLayout lawsuitLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.main_activity);
    AppBarLayout toolbarContent = findViewById(R.id.toolbar_content);
    Toolbar toolbar = toolbarContent.findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    TextView toolbarTitle = toolbarContent.findViewById(R.id.toolbar_title);
    toolbarTitle.setText(toolbar.getTitle());
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(false);

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

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    recyclerView = findViewById(R.id.recycler_view);

    // use a linear layout manager
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    // specify an adapter (see also next example)
    adapter = new MessagesAdapter(this);
    recyclerView.setAdapter(adapter);

    // Set summary card:
    summaryCard = findViewById(R.id.summary_layout);
    summaryTextView = summaryCard.findViewById(R.id.status_summary);
    lawsuitLayout = summaryCard.findViewById(R.id.lawsuit);
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_about) {
      openAbout();
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void openAbout() {
    Log.d(TAG, "openAbout");
    AboutActivity.startActivity(this);
  }

  @Override
  public void onFetched() {
    int messageCount = adapter.getItemCount();
    if (messageCount == 0) {
      summaryTextView.setText(getResources().getString(R.string.content_summary_empty));
      lawsuitLayout.setVisibility(View.GONE);
    } else {
      summaryTextView.setText(
          Html.fromHtml(
              getResources().getString(R.string.content_summary, String.valueOf(messageCount))));
      lawsuitLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onDestroy() {
    adapter.deAttachContext();
  }
}

