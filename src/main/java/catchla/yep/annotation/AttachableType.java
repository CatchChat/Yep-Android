package catchla.yep.annotation;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 16/6/3.
 */
@StringDef({AttachableType.MESSAGE, AttachableType.TOPIC})
public @interface AttachableType {
    String MESSAGE = "Message";
    String TOPIC = "Topic";
}
