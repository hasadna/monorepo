package hasadna.noloan.admin.app;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPageAdapter extends FragmentPagerAdapter {

  private List<Fragment> fragments;
  private List<String> titles;

  public MainActivityPageAdapter(FragmentManager fm) {
    super(fm);
    fragments = new ArrayList<>();
    titles = new ArrayList<>();
  }

  public void addFragment(Fragment fragment, String title) {
    fragments.add(fragment);
    titles.add(title);
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return titles.get(position);
  }

  @Override
  public Fragment getItem(int i) {
    return fragments.get(i);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }
}

