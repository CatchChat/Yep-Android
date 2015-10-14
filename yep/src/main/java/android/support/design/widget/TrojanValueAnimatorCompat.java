package android.support.design.widget;

import android.support.v4.view.ViewPropertyAnimatorCompat;

import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/10/14.
 */
public class TrojanValueAnimatorCompat extends ValueAnimatorCompat {
    TrojanValueAnimatorCompat(final ValueAnimatorCompat delegated) {
        super(findImpl(delegated));
    }

    private static Impl findImpl(final ValueAnimatorCompat delegated) {
        return (Impl) Utils.findFieldOfTypes(delegated, ViewPropertyAnimatorCompat.class, Impl.class);
    }


    public static abstract class TrojanAnimatorUpdateListener implements ValueAnimatorCompat.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(final ValueAnimatorCompat valueAnimatorCompat) {
            onAnimationUpdate((TrojanValueAnimatorCompat) valueAnimatorCompat);
        }

        public abstract void onAnimationUpdate(TrojanValueAnimatorCompat var1);
    }
}
