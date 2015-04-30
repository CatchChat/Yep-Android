/*
 * Copyright (c) 2015. Catch Inc,
 */

package android.support.v7.widget;

import android.content.Context;
import android.view.View;

/**
 * Created by mariotaku on 15/3/24.
 */
public class FixedLinearLayoutManager extends LinearLayoutManager {

    public FixedLinearLayoutManager(Context context) {
        super(context);
    }

    public FixedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible, boolean acceptPartiallyVisible) {
        // XXX Fixed NPE by add a simple check to child view count
        if (getChildCount() < 0) return null;
        return super.findOneVisibleChild(fromIndex, toIndex, completelyVisible, acceptPartiallyVisible);
    }

}
