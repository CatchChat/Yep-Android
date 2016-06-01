/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.fragment.UserRankFragment
import catchla.yep.fragment.UserSuggestionsFragment
import catchla.yep.model.AccessToken
import catchla.yep.util.ThemeUtils
import catchla.yep.util.Utils
import catchla.yep.view.TabPagerIndicator
import catchla.yep.view.TintedStatusFrameLayout

class WelcomeActivity : AccountAuthenticatorActivity(), Constants, View.OnClickListener {

    private lateinit var mViewPager: ViewPager
    private lateinit var mAdapter: TabsAdapter
    private lateinit var mPagerIndicator: TabPagerIndicator
    private lateinit var mMainContent: TintedStatusFrameLayout
    private lateinit var mSignInButton: Button
    private lateinit var mSignUpButton: Button

    override fun onContentChanged() {
        super.onContentChanged()
        mMainContent = findViewById(R.id.main_content) as TintedStatusFrameLayout
        mViewPager = findViewById(R.id.view_pager) as ViewPager
        mSignInButton = findViewById(R.id.sign_in) as Button
        mSignUpButton = findViewById(R.id.sign_up) as Button
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_ADD_ACCOUNT -> {
                if (resultCode != Activity.RESULT_OK) return
                val token = data.getParcelableExtra<AccessToken>(Constants.EXTRA_TOKEN)
                val user = token.user
                val account = Account(user.mobile, Constants.ACCOUNT_TYPE)
                val userData = Bundle()
                Utils.writeUserToUserData(user, userData)
                val am = AccountManager.get(this)
                am.addAccountExplicitly(account, null, userData)
                am.setAuthToken(account, Constants.AUTH_TOKEN_TYPE, token.accessToken)
                if (Utils.getCurrentAccount(this) == null) {
                    Utils.setCurrentAccount(this, account)
                }
                val result = Bundle()
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
                setAccountAuthenticatorResult(result)
                if (!intent.hasExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)) {
                    val launcherIntent = Intent(this, MainActivity::class.java)
                    startActivity(launcherIntent)
                }
                finish()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.layout_welcome_tabs)
        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))
        mPagerIndicator = actionBar.customView.findViewById(R.id.pager_indicator) as TabPagerIndicator
        setContentView(R.layout.activity_welcome)
        val toolbar = window.findViewById(android.support.v7.appcompat.R.id.action_bar) as Toolbar
        toolbar.setContentInsetsRelative(0, 0)

        mSignInButton.setOnClickListener(this)
        mSignUpButton.setOnClickListener(this)

        mAdapter = TabsAdapter(actionBar.themedContext, supportFragmentManager)
        mViewPager.adapter = mAdapter
        mViewPager.offscreenPageLimit = 2
        mMainContent.setStatusBarColor(primaryColor)
        mAdapter.addTab(UserSuggestionsFragment::class.java, getString(R.string.suggestions), 0, null)
        mAdapter.addTab(UserRankFragment::class.java, getString(R.string.rank), 0, null)
        mPagerIndicator.setViewPager(mViewPager)
        mPagerIndicator.updateAppearance()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in -> {
                startActivityForResult(Intent(this, SignInActivity::class.java), REQUEST_ADD_ACCOUNT)
            }
            R.id.sign_up -> {
                startActivityForResult(Intent(this, SignUpActivity::class.java), REQUEST_ADD_ACCOUNT)
            }
        }
    }

    companion object {

        private val REQUEST_ADD_ACCOUNT = 101
    }

}
