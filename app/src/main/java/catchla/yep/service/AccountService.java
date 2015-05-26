package catchla.yep.service;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.activity.SignInActivity;
import catchla.yep.activity.WelcomeActivity;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

public class AccountService extends Service implements Constants {

    private CatchLaAccountAuthenticator mAccountAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        if (mAccountAuthenticator == null) {
            mAccountAuthenticator = new CatchLaAccountAuthenticator(this);
        }
        return mAccountAuthenticator.getIBinder();
    }

    private static class CatchLaAccountAuthenticator extends AbstractAccountAuthenticator {

        private final Context mContext;
        private final AccountManager mAccountManager;

        CatchLaAccountAuthenticator(final Context context) {
            super(context);
            mContext = context;
            mAccountManager = AccountManager.get(context);
        }

        @Override
        public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
            return null;
        }

        @Override
        public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType,
                                 final String authTokenType, final String[] requiredFeatures, final Bundle options)
                throws NetworkErrorException {
            final Bundle reply = new Bundle();
            final Intent intent = new Intent(mContext, WelcomeActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            reply.putParcelable(AccountManager.KEY_INTENT, intent);
            return reply;
        }

        @Override
        public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account,
                                         final Bundle options) throws NetworkErrorException {
            try {
                final YepAPI yep = YepAPIFactory.getInstance(mContext, account);
                yep.getUser();
                final Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
                return result;
            } catch (final YepException e) {
                if (e.getCause() instanceof IOException) throw new NetworkErrorException(e);
                final Bundle result = new Bundle();
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
                return result;
            }
        }

        @Override
        public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account,
                                   final String authTokenType, final Bundle options) throws NetworkErrorException {
            final Bundle result = new Bundle();
            Intent intent = new Intent(mContext, SignInActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
            return result;
        }

        @Override
        public String getAuthTokenLabel(final String authTokenType) {
            return null;
        }

        @Override
        public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account,
                                        final String authTokenType, final Bundle options) throws NetworkErrorException {
            final Bundle result = new Bundle();
            Intent intent = new Intent(mContext, SignInActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
            return result;
        }

        @Override
        public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account,
                                  final String[] features) throws NetworkErrorException {
            final Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
            return result;
        }

        @NonNull
        @Override
        public Bundle getAccountRemovalAllowed(final AccountAuthenticatorResponse response, final Account account)
                throws NetworkErrorException {
            final String accountId = mAccountManager.getUserData(account, USER_DATA_ID);
            final Bundle result = super.getAccountRemovalAllowed(response, account);
            if (result.containsKey(AccountManager.KEY_BOOLEAN_RESULT) && !result.containsKey(AccountManager.KEY_INTENT)) {
                final boolean removalAllowed = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT);
                if (removalAllowed) {
//                    JPushInterface.setAlias(mContext.getApplicationContext(), "", new TagAliasCallback() {
//                        @Override
//                        public void gotResult(int statusCode, String s, Set<String> strings) {
//
//                        }
//                    });
                    if (BuildConfig.DEBUG) {
                        Log.d(LOGTAG, String.format("account %s removed, id: %s", account, accountId));
                    }
                }
            }
            return result;
        }


    }
}
