package com.nusantara.wings.bottomnav;

import android.graphics.Typeface;
import com.nusantara.wings.bottomnav.BubbleNavigationChangeListener;

public interface IBubbleNavigation {
    void setNavigationChangeListener(BubbleNavigationChangeListener navigationChangeListener);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);
}
