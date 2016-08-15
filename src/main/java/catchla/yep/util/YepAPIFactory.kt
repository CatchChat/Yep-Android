package catchla.yep.util

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import android.text.TextUtils
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.model.YepException
import okhttp3.OkHttpClient
import org.apache.commons.lang3.math.NumberUtils
import org.mariotaku.ktextension.bcp47Tag
import org.mariotaku.ktextension.initCause
import org.mariotaku.restfu.ExceptionFactory
import org.mariotaku.restfu.RestAPIFactory
import org.mariotaku.restfu.RestRequest
import org.mariotaku.restfu.http.Authorization
import org.mariotaku.restfu.http.Endpoint
import org.mariotaku.restfu.http.SimpleValueMap
import org.mariotaku.restfu.okhttp3.OkHttpRestClient
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*

/**
 * Created by mariotaku on 15/5/23.
 */
class YepAPIFactory : Constants {
    companion object {

        val API_DOMAIN = "api.soyep.com"

        fun getInstance(context: Context, account: Account): YepAPI {
            return getInstanceWithToken(context, getAuthToken(context, account))
        }

        fun getInstanceWithToken(context: Context, accessToken: String?): YepAPI {
            val factory = RestAPIFactory<YepException>()
            val client = getOkHttpClient(context)
            factory.setHttpClient(OkHttpRestClient(client))
            factory.setEndpoint(Endpoint(BuildConfig.API_ENDPOINT_REST))
            factory.setRestConverterFactory(YepConverterFactory())
            factory.setExceptionFactory(ExceptionFactory<YepException> { cause, request, response ->
                var exception: YepException? = null
                if (response != null) {
                    try {
                        exception = JsonSerializer.parse(response.body.stream(), YepException::class.java)
                    } catch (e: IOException) {
                        // Ignore
                    }

                }
                if (exception == null) {
                    exception = YepException()
                }
                if (cause != null) {
                    exception.initCause(cause)
                }
                exception.request = request
                exception.response = response
                exception
            })
            factory.setAuthorization(object : Authorization {
                override fun getHeader(endpoint: Endpoint, info: RestRequest): String {
                    return getAuthorizationHeaderValue(accessToken!!)
                }

                override fun hasAuthorization(): Boolean {
                    return accessToken != null
                }
            })
            val constantPool = SimpleValueMap()
            constantPool.put("accept_language", Locale.getDefault().bcp47Tag)
            factory.setConstantPool(constantPool)

            return factory.build(YepAPI::class.java)
        }

        @SuppressLint("SSLCertificateSocketFactoryGetInsecure")
        fun getOkHttpClient(context: Context): OkHttpClient {
            val builder = OkHttpClient.Builder()
            DebugModeUtils.initForHttpClient(builder)
            if (BuildConfig.DEBUG) {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                if (preferences.getBoolean("proxy", false)) {
                    val host = preferences.getString("proxy_host", null)
                    val port = NumberUtils.toInt(preferences.getString("proxy_port", null), -1)
                    if (!TextUtils.isEmpty(host) && port >= 0 && port < 65536) {
                        builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host,
                                port)))
                    }
                }
            }
            return builder.build()
        }

        fun getProviderOAuthUrl(providerName: String): String {
            return "https://$API_DOMAIN/users/auth/$providerName"
        }

        fun getAuthToken(context: Context, account: Account?): String? {
            if (account == null) return null
            val am = AccountManager.get(context)
            return am.peekAuthToken(account, Constants.AUTH_TOKEN_TYPE)
        }

        fun isAPIUrl(uri: Uri): Boolean {
            return API_DOMAIN.equals(uri.host, ignoreCase = true)
        }

        fun isAuthSuccessUrl(url: String): Boolean {
            return "yep://auth/success" == url
        }

        fun isAuthFailureUrl(url: String): Boolean {
            return "yep://auth/failure" == url
        }

        fun getAuthorizationHeaderValue(accessToken: String): String {
            return String.format(Locale.ROOT, "Token token=\"%s\"", accessToken)
        }
    }

}
