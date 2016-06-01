/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils

import catchla.yep.Constants
import catchla.yep.util.Utils


class MainActivity : Activity(), Constants {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SELECT_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
                    val accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    val currentAccount = Account(accountName, accountType)
                    Utils.setCurrentAccount(this, currentAccount)
                    val homeIntent = Intent(this, MainActivity::class.java)
                    homeIntent.putExtras(intent)
                    //                    homeIntent.putExtra(EXTRA_REFRESH_FRIEND_LIST, getUsersCount(currentAccount) == 0);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(homeIntent)
                }
                finish()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Crashlytics.start(this);
        val currentAccount = Utils.getCurrentAccount(this)
        if (currentAccount == null) {
            val am = AccountManager.get(this)
            val accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE)
            if (accounts.size == 0) {
                val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                welcomeIntent.putExtras(intent)
                welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(welcomeIntent)
            } else {
                val allowableAccountTypes = arrayOf(Constants.ACCOUNT_TYPE)
                val intent = AccountManager.newChooseAccountIntent(null, null,
                        allowableAccountTypes, true, null, null, null, null)
                startActivityForResult(intent, REQUEST_SELECT_ACCOUNT)
                return
            }
        } else if (!isAccountValid(this, currentAccount)) {
            val am = AccountManager.get(this)
            am.removeAccount(currentAccount, { }, Handler())
            val welcomeIntent = Intent(this, WelcomeActivity::class.java)
            welcomeIntent.putExtras(intent)
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(welcomeIntent)
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.putExtras(intent)
            //            homeIntent.putExtra(EXTRA_REFRESH_FRIEND_LIST, true);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(homeIntent)
        }
        finish()
    }

    companion object {

        private val REQUEST_SELECT_ACCOUNT = 101

        private fun isAccountValid(context: Context, account: Account): Boolean {
            val am = AccountManager.get(context)
            if (TextUtils.isEmpty(am.getUserData(account, Constants.USER_DATA_ID))) return false
            return true
        }
    }

}
