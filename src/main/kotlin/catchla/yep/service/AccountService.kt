package catchla.yep.service

import android.accounts.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.activity.SignInActivity
import catchla.yep.activity.WelcomeActivity
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory
import java.io.IOException

class AccountService : Service(), Constants {

    private val accountAuthenticator: CatchLaAccountAuthenticator by  lazy { CatchLaAccountAuthenticator(this) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        return accountAuthenticator.iBinder
    }

    private class CatchLaAccountAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
        private val accountManager: AccountManager

        init {
            accountManager = AccountManager.get(context)
        }

        override fun editProperties(response: AccountAuthenticatorResponse, accountType: String): Bundle? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun addAccount(response: AccountAuthenticatorResponse, accountType: String,
                                authTokenType: String, requiredFeatures: Array<String>, options: Bundle): Bundle {
            val reply = Bundle()
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            reply.putParcelable(AccountManager.KEY_INTENT, intent)
            return reply
        }

        @Throws(NetworkErrorException::class)
        override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account,
                                        options: Bundle): Bundle {
            try {
                val yep = YepAPIFactory.getInstance(context, account)
                yep.getUser()
                val result = Bundle()
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
                return result
            } catch (e: YepException) {
                if (e.cause is IOException) throw NetworkErrorException(e)
                val result = Bundle()
                result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
                return result
            }

        }

        @Throws(NetworkErrorException::class)
        override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account,
                                  authTokenType: String, options: Bundle): Bundle {
            val result = Bundle()
            val intent = Intent(context, SignInActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            result.putParcelable(AccountManager.KEY_INTENT, intent)
            return result
        }

        override fun getAuthTokenLabel(authTokenType: String): String? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account,
                                       authTokenType: String, options: Bundle): Bundle {
            val result = Bundle()
            val intent = Intent(context, SignInActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            result.putParcelable(AccountManager.KEY_INTENT, intent)
            return result
        }

        @Throws(NetworkErrorException::class)
        override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account,
                                 features: Array<String>): Bundle {
            val result = Bundle()
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
            return result
        }

        @Throws(NetworkErrorException::class)
        override fun getAccountRemovalAllowed(response: AccountAuthenticatorResponse, account: Account): Bundle {
            val accountId = accountManager.getUserData(account, Constants.USER_DATA_ID)
            val token = accountManager.peekAuthToken(account, Constants.AUTH_TOKEN_TYPE)
            val result = super.getAccountRemovalAllowed(response, account)
            if (result.containsKey(AccountManager.KEY_BOOLEAN_RESULT) && !result.containsKey(AccountManager.KEY_INTENT)) {
                val removalAllowed = result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT)
                if (removalAllowed) {
                    val yepAPI = YepAPIFactory.getInstanceWithToken(context, token)
                    try {
                        if (!yepAPI.logout().isSuccessful) {
                            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
                            return result
                        }
                    } catch (e: YepException) {
                        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
                        return result
                    }

                    if (BuildConfig.DEBUG) {
                        Log.d(Constants.LOGTAG, String.format("account %s removed, id: %s", account, accountId))
                    }
                }
            }
            return result
        }


    }
}
