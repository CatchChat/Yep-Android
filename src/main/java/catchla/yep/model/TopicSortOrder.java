package catchla.yep.model;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 16/7/20.
 */
@StringDef({TopicSortOrder.DEFAULT, TopicSortOrder.DISTANCE, TopicSortOrder.TIME})
public @interface TopicSortOrder {
    String DEFAULT = "default";
    String DISTANCE = "distance";
    String TIME = "time";
}
