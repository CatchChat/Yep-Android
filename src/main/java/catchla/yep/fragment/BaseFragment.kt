/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import catchla.yep.Constants
import catchla.yep.fragment.iface.IBaseFragment
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.dagger.GeneralComponentHelper
import com.squareup.otto.Bus
import javax.inject.Inject


open class BaseFragment : Fragment(), IBaseFragment, Constants {

    @Inject
    lateinit var mBus: Bus
    @Inject
    lateinit var mImageLoader: ImageLoaderWrapper
    @Inject
    lateinit var mPreferences: SharedPreferences

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBaseViewCreated(view!!, savedInstanceState)
        requestFitSystemWindows()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        GeneralComponentHelper.build(context!!).inject(this)
    }

    override fun requestFitSystemWindows() {
        val activity = activity
        val parentFragment = parentFragment
        val callback: IBaseFragment.SystemWindowsInsetsCallback
        if (parentFragment is IBaseFragment.SystemWindowsInsetsCallback) {
            callback = parentFragment
        } else if (activity is IBaseFragment.SystemWindowsInsetsCallback) {
            callback = activity
        } else {
            return
        }
        val insets = Rect()
        if (callback.getSystemWindowsInsets(insets)) {
            fitSystemWindows(insets)
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    protected open fun fitSystemWindows(insets: Rect) {
        val view = view
        view?.setPadding(insets.left, insets.top, insets.right, insets.bottom)
    }
}
