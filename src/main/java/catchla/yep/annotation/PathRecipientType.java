package catchla.yep.annotation;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 16/6/3.
 */
@StringDef({PathRecipientType.USERS, PathRecipientType.CIRCLES})
public @interface PathRecipientType {
    String USERS = "users";
    String CIRCLES = "circles";
}
