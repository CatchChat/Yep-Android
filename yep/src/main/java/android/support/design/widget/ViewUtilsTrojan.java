package android.support.design.widget;


/**
 * Created by mariotaku on 15/10/14.
 */
public class ViewUtilsTrojan {
    public static TrojanValueAnimatorCompat createAnimator() {
        return new TrojanValueAnimatorCompat(ViewUtils.createAnimator());
    }
}
