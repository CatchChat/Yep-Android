package android.support.design.widget;

import android.view.animation.Interpolator;

/**
 * Created by mariotaku on 15/10/14.
 */
public class AnimationUtilsTrojan {


    public static final Interpolator LINEAR_INTERPOLATOR = AnimationUtils.LINEAR_INTERPOLATOR;
    public static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR;
    public static final Interpolator DECELERATE_INTERPOLATOR = AnimationUtils.DECELERATE_INTERPOLATOR;

    public static float lerp(float startValue, float endValue, float fraction) {
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    public static int lerp(int startValue, int endValue, float fraction) {
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }
}
