package android.support.design.widget;

import android.support.v4.view.ViewPropertyAnimatorCompat;

import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/10/14.
 */
public class AccessorValueAnimatorCompat extends ValueAnimatorCompat {
    AccessorValueAnimatorCompat(final ValueAnimatorCompat delegated) {
        super(findImpl(delegated));
    }

    private static Impl findImpl(final ValueAnimatorCompat delegated) {
        return (Impl) Utils.findFieldOfTypes(delegated, ViewPropertyAnimatorCompat.class, Impl.class);
    }


    public static abstract class AccessorAnimatorUpdateListener implements ValueAnimatorCompat.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(final ValueAnimatorCompat valueAnimatorCompat) {
            onAnimationUpdate((AccessorValueAnimatorCompat) valueAnimatorCompat);
        }

        public abstract void onAnimationUpdate(AccessorValueAnimatorCompat var1);
    }
}
