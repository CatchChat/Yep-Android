/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.extension.Bundle
import catchla.yep.fragment.UserRankFragment
import catchla.yep.fragment.UserSuggestionsFragment
import catchla.yep.model.AccessToken
import catchla.yep.util.ThemeUtils
import catchla.yep.util.Utils
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AccountAuthenticatorActivity(), Constants, View.OnClickListener {

    private val REQUEST_ADD_ACCOUNT = 101

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ADD_ACCOUNT -> {
                if (resultCode != Activity.RESULT_OK) return
                val token = data!!.getParcelableExtra<AccessToken>(Constants.EXTRA_TOKEN)
                val user = token.user
                val account = Account(user!!.mobile, Constants.ACCOUNT_TYPE)
                val userData = Bundle {
                    Utils.writeUserToUserData(user, this)
                }
                val am = AccountManager.get(this)
                am.addAccountExplicitly(account, null, userData)
                am.setAuthToken(account, Constants.AUTH_TOKEN_TYPE, token.accessToken)
                if (Utils.getCurrentAccount(this) == null) {
                    Utils.setCurrentAccount(this, account)
                }
                val result = Bundle {
                    putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
                }
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
        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        setContentView(R.layout.activity_welcome)

        signIn.setOnClickListener(this)
        signUp.setOnClickListener(this)

        val adapter = TabsAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        mainContent!!.setStatusBarColorDarken(primaryColor)
        adapter.addTab(UserSuggestionsFragment::class.java, getString(R.string.suggestions), 0, null)
        adapter.addTab(UserRankFragment::class.java, getString(R.string.rank), 0, null)
    }

    override fun onClick(v: View) {
        when (v) {
            signIn -> {
                startActivityForResult(Intent(this, SignInActivity::class.java), REQUEST_ADD_ACCOUNT)
            }
            signUp -> {
                startActivityForResult(Intent(this, SignUpActivity::class.java), REQUEST_ADD_ACCOUNT)
            }
        }
    }

}
