/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment.iface;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

public interface IBaseFragment {

    void requestFitSystemWindows();

    void onBaseViewCreated(View view, Bundle savedInstanceState);

    interface SystemWindowsInsetsCallback {
        boolean getSystemWindowsInsets(Rect insets);
    }
}
