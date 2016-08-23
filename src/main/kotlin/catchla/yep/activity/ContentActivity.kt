package catchla.yep.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import catchla.yep.R
import catchla.yep.activity.iface.IBaseActivity
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.ThemeUtils
import catchla.yep.util.dagger.GeneralComponentHelper
import catchla.yep.util.support.WindowSupport
import catchla.yep.view.TintedStatusFrameLayout
import com.squareup.otto.Bus
import javax.inject.Inject

open class ContentActivity : AppCompatActivity(), IBaseActivity {

    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var imageLoader: ImageLoaderWrapper

    private val actionHelper = IBaseActivity.ActionHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowSupport.setStatusBarColor(window, Color.TRANSPARENT)
        GeneralComponentHelper.build(this).inject(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupTintStatusBar()
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }


    override fun onResumeFragments() {
        super.onResumeFragments()
        actionHelper.dispatchOnResumeFragments()
    }

    override fun executeAfterFragmentResumed(action: (IBaseActivity) -> Unit) {
        actionHelper.executeAfterFragmentResumed(action)
    }

    override fun onPause() {
        actionHelper.dispatchOnPause()
        super.onPause()
    }

    protected open val isTintBarEnabled: Boolean
        get() = true

    private fun setupTintStatusBar() {
        val actionBar = supportActionBar
        val mainContent = findViewById(R.id.mainContent) as TintedStatusFrameLayout?
        if (mainContent == null || actionBar == null || !isTintBarEnabled) return

        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))
        mainContent.setStatusBarColorDarken(primaryColor)
    }

}
