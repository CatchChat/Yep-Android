package catchla.yep.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by mariotaku on 15/5/21.
 */
public class TabSpec {
   public Class<? extends Fragment> cls;
    public CharSequence title;
    public int icon;
    public Bundle args;

    public TabSpec(Class<? extends Fragment> cls, CharSequence title, int icon, Bundle args) {
        this.cls = cls;
        this.title = title;
        this.icon = icon;
        this.args = args;
    }

}
