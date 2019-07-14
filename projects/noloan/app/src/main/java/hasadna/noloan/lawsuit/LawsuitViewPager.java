package hasadna.noloan.lawsuit;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

// Extension for disabling swipe motion between fragments
public class LawsuitViewPager extends ViewPager {

  private boolean swipeable;

  public LawsuitViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.swipeable = true;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return swipeable && super.onTouchEvent(event);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return this.swipeable && super.onInterceptTouchEvent(event);
  }

  public void setPagingEnabled(boolean enabled) {
    this.swipeable = enabled;
  }
}

