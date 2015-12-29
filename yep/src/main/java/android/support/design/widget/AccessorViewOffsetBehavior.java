package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mariotaku on 15/10/14.
 */
public class AccessorViewOffsetBehavior<V extends View> extends ViewOffsetBehavior<V> {
    public AccessorViewOffsetBehavior() {
        super();
    }

    public AccessorViewOffsetBehavior(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
}
