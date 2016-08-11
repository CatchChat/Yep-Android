package catchla.yep.annotation;

import android.support.annotation.IntDef;

/**
 * Created by mariotaku on 16/8/11.
 */
@IntDef({ItemType.USER, ItemType.MESSAGE, ItemType.TOPIC})
public @interface ItemType {
    int USER = 1;
    int MESSAGE = 2;
    int TOPIC = 3;
}
