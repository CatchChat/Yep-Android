package catchla.yep.activity

import android.accounts.Account
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity

import com.squareup.otto.Bus

import javax.inject.Inject

import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.ThemeUtils
import catchla.yep.util.dagger.GeneralComponentHelper
import catchla.yep.view.TintedStatusFrameLayout

open class ContentActivity : AppCompatActivity() {

    protected var mainContent: TintedStatusFrameLayout? = null
        private set
    @Inject
    lateinit var mBus: Bus
    @Inject
    lateinit var mImageLoader: ImageLoaderWrapper

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
        mainContent = findViewById(R.id.main_content) as TintedStatusFrameLayout?
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
