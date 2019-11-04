package hasadna.noloan.mainactivity;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import noloan.R;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

  private Context context;

  public MainActivityPagerAdapter(FragmentManager fragmentManager, Context context) {
    super(fragmentManager);
    this.context = context;
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return new InboxFragment();
      case 1:
        return new SpamFragment();
      default:
        return null;
    }
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return context.getString(R.string.inboxFragment_title);
      case 1:
        return context.getString(R.string.spamFragment_title);
      default:
        return null;
    }
  }
}

