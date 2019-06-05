package hasadna.noloan2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LawsuitFragmentAdapter extends FragmentPagerAdapter {

  public LawsuitFragmentAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  // Choose a fragment to display on lawsuit main page
  @Override
  public Fragment getItem(int position) {
    Fragment fragment = null;
    switch (position) {
      case 0:
        fragment = new LawsuitFormFragment();
        break;
      case 1:
        fragment = new LawsuitSummaryFragment();
        break;
      case 2:
        fragment = new LawsuitClaimFragment();
        break;
    }
    return fragment;
  }

  @Override
  public int getCount() {
    return 3;
  }
}

