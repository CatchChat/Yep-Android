package android.support.v4.app;

import android.os.Bundle;

public class FragmentAccessor {

    public static Bundle getSavedFragmentState(final Fragment f) {
        return f.mSavedFragmentState;
    }

}
