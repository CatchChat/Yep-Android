package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.Uri;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.mariotaku.restfu.RestAPIFactory;
import org.mariotaku.restfu.RestRequest;
import org.mariotaku.restfu.http.Authorization;
import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.restfu.okhttp.OkHttpRestClient;

import java.io.IOException;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepAPIFactory implements Constants {

    public static final String API_DOMAIN = "api.soyep.com";
    public static final String API_ENDPOINT_FAYE = "https://faye.catchchatchina.com/faye";

    public static YepAPI getInstance(Context context, Account account) {
        if (account == null) return null;
        return getInstanceWithToken(context, getAuthToken(context, account));
    }

    public static YepAPI getInstanceWithToken(final Context context, final String accessToken) {
        RestAPIFactory<YepException> factory = new RestAPIFactory<>();
        final OkHttpClient client = getOkHttpClient(context);
        factory.setHttpClient(new OkHttpRestClient(client));
        factory.setEndpoint(new Endpoint(BuildConfig.API_ENDPOINT_REST));
        factory.setRestConverterFactory(new YepConverterFactory());
        factory.setAuthorization(new Authorization() {
            @Override
            public String getHeader(final Endpoint endpoint, final RestRequest info) {
                return getAuthorizationHeaderValue(accessToken);
            }

            @Override
            public boolean hasAuthorization() {
                return accessToken != null;
            }
        });

        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.header("Accept", "application/json");
                builder.header("Accept-Language", Locale.getDefault().toString());
                return chain.proceed(builder.build());
            }
        });
        return factory.build(YepAPI.class);
    }

    @SuppressLint("SSLCertificateSocketFactoryGetInsecure")
    public static OkHttpClient getOkHttpClient(Context context) {
        final OkHttpClient client = new OkHttpClient();
        DebugModeUtils.initForHttpClient(client);
        if (BuildConfig.IS_STAGING) {
            client.setSslSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
            client.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
        }
        return client;
    }

    public static String getProviderOAuthUrl(final String providerName) {
        return "https://" + API_DOMAIN + "/users/auth/" + providerName;
    }

    public static String getAuthToken(final Context context, final Account account) {
        if (account == null) return null;
        final AccountManager am = AccountManager.get(context);
        return am.peekAuthToken(account, AUTH_TOKEN_TYPE);
    }

    public static boolean isAPIUrl(final Uri uri) {
        return API_DOMAIN.equalsIgnoreCase(uri.getHost());
    }

    public static boolean isAuthSuccessUrl(final String url) {
        return "yep://auth/success".equals(url);
    }

    public static boolean isAuthFailureUrl(final String url) {
        return "yep://auth/failure".equals(url);
    }

    public static String getAuthorizationHeaderValue(String accessToken) {
        return String.format(Locale.ROOT, "Token token=\"%s\"", accessToken);
    }

}
