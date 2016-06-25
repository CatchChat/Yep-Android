package catchla.yep.activity

import android.accounts.Account
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.ThemeUtils
import catchla.yep.util.dagger.GeneralComponentHelper
import catchla.yep.view.TintedStatusFrameLayout
import com.squareup.otto.Bus
import javax.inject.Inject

open class ContentActivity : AppCompatActivity() {

    protected val mainContent by lazy { findViewById(R.id.mainContent) as TintedStatusFrameLayout? }
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var imageLoader: ImageLoaderWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneralComponentHelper.build(this).inject(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupTintStatusBar()
    }

    val account: Account
        get() = intent.getParcelableExtra<Account>(Constants.EXTRA_ACCOUNT)

    override fun onContentChanged() {
        super.onContentChanged()
    }

    protected open val isTintBarEnabled: Boolean
        get() = true

    private fun setupTintStatusBar() {
        val actionBar = supportActionBar
        if (mainContent == null || actionBar == null || !isTintBarEnabled) return

        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))
        mainContent!!.setStatusBarColor(primaryColor)
    }

}
