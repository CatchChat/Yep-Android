package catchla.yep.model;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 16/7/20.
 */
@StringDef({DiscoverSortOrder.SCORE, DiscoverSortOrder.DISTANCE, DiscoverSortOrder.LAST_SIGN_IN_AT})
public @interface DiscoverSortOrder {
    String SCORE = "score";
    String DISTANCE = "distance";
    String LAST_SIGN_IN_AT = "last_sign_in_at";
}
