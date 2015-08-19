/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import catchla.yep.Constants;
import catchla.yep.util.Utils;


public class MainActivity extends AppCompatActivity implements Constants {

    private static final int REQUEST_SELECT_ACCOUNT = 101;

    private static boolean isAccountValid(Context context, Account account) {
        final AccountManager am = AccountManager.get(context);
        if (TextUtils.isEmpty(am.getUserData(account, USER_DATA_ID))) return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_ACCOUNT: {
                if (resultCode == RESULT_OK && data != null && data.hasExtra(AccountManager.KEY_ACCOUNT_NAME)) {
                    String accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    final Account currentAccount = new Account(accountName, accountType);
                    Utils.setCurrentAccount(this, currentAccount);
                    final Intent homeIntent = new Intent(this, MainActivity.class);
                    homeIntent.putExtras(getIntent());
//                    homeIntent.putExtra(EXTRA_REFRESH_FRIEND_LIST, getUsersCount(currentAccount) == 0);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(homeIntent);
                }
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Crashlytics.start(this);
        final Account currentAccount = Utils.getCurrentAccount(this);
        if (currentAccount == null) {
            final AccountManager am = AccountManager.get(this);
            final Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
            if (accounts.length == 0) {
                final Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
                welcomeIntent.putExtras(getIntent());
                welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(welcomeIntent);
            } else {
                final String[] allowableAccountTypes = {ACCOUNT_TYPE};
                final Intent intent = AccountManager.newChooseAccountIntent(null, null,
                        allowableAccountTypes, true, null, null, null, null);
                startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
                return;
            }
        } else if (!isAccountValid(this, currentAccount)) {
            final AccountManager am = AccountManager.get(this);
            am.removeAccount(currentAccount, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {

                }
            }, new Handler());
            final Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
            welcomeIntent.putExtras(getIntent());
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(welcomeIntent);
        } else {
            final Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtras(getIntent());
//            homeIntent.putExtra(EXTRA_REFRESH_FRIEND_LIST, true);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(homeIntent);
        }
        finish();
    }

}
