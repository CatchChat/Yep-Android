package catchla.yep.view;

import android.content.Context;
import android.util.AttributeSet;

import com.commonsware.cwac.layouts.AspectLockedFrameLayout;

/**
 * Created by mariotaku on 16/1/30.
 */
public class LocationContainerFrameLayout extends AspectLockedFrameLayout {
    public LocationContainerFrameLayout(final Context context) {
        this(context, null);
    }

    public LocationContainerFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setAspectRatio(2.5);
    }
}
