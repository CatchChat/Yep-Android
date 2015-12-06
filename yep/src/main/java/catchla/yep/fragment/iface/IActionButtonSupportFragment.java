package catchla.yep.fragment.iface;

import android.support.annotation.Nullable;

import catchla.yep.fragment.FloatingActionMenuFragment;

/**
 * Created by mariotaku on 15/10/13.
 */
public interface IActionButtonSupportFragment {

    int getActionIcon();

    void onActionPerformed();

    @Nullable
    Class<? extends FloatingActionMenuFragment> getActionMenuFragment();

}
