package hasadna.noloan.mainactivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivityPagerAdapter extends FragmentPagerAdapter {

  private List<Fragment> fragments;
  private List<String> titles;

  public MainActivityPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
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

