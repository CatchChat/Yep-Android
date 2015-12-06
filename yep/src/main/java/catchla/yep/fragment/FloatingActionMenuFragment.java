package catchla.yep.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by mariotaku on 15/12/6.
 */
public class FloatingActionMenuFragment extends BaseFragment {
    private Fragment mBelongsTo;

    public Fragment getBelongsTo() {
        return mBelongsTo;
    }

    public void setBelongsTo(final Fragment fragment) {
        mBelongsTo = fragment;
    }
}
