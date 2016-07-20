package catchla.yep.model;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 16/7/20.
 */
@StringDef({SortOrder.DEFAULT, SortOrder.DISTANCE, SortOrder.TIME})
public @interface SortOrder {
    String DEFAULT = "default";
    String DISTANCE = "distance";
    String TIME = "time";
}
