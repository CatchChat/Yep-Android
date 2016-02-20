package android.support.design.widget;


/**
 * Created by mariotaku on 15/10/14.
 */
public class ViewUtilsAccessor {
    public static AccessorValueAnimatorCompat createAnimator() {
        return new AccessorValueAnimatorCompat(ViewUtils.createAnimator());
    }
}
