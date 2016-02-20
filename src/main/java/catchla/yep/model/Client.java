package catchla.yep.model;

import android.support.annotation.IntDef;

/**
 * Created by mariotaku on 15/5/23.
 */
@IntDef({Client.OFFICIAL, Client.COMPANY, Client.LOCAL})
public @interface Client {

    int OFFICIAL = 0;
    int COMPANY = 1;
    int LOCAL = 2;

}
