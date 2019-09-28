package hasadna.noloan.admin.app;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  private static final String TAG = "MainActivity";

  private DrawerLayout drawerLayout;

  private RecyclerView recycler;

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

    ViewPager viewPager = findViewById(R.id.viewPager);
    // RTL swiping (Along with recyclerView.setRotationY(180) in fragments)
    viewPager.setRotationY(180);
    MainActivityPageAdapter pageAdapter =
            new MainActivityPageAdapter(getSupportFragmentManager());
    pageAdapter.addFragment(new SpamFragment(),"spam");
    pageAdapter.addFragment(new SuggetionsFragment(),"suggestions");
    viewPager.setAdapter(pageAdapter);
    TabLayout tabLayout = findViewById(R.id.TabLayout);
    tabLayout.setupWithViewPager(viewPager);
  }

  // Handle navigation view item clicks here.
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_about) {
      openAbout();
    }
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void openAbout() {
    AboutActivity.startActivity(this);
  }
}

