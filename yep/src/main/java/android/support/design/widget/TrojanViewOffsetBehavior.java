package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mariotaku on 15/10/14.
 */
public class TrojanViewOffsetBehavior<V extends View> extends ViewOffsetBehavior<V> {
    public TrojanViewOffsetBehavior() {
        super();
    }

    public TrojanViewOffsetBehavior(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
}
