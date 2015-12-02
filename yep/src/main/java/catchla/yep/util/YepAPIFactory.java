package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import catchla.yep.Constants;
import retrofit.Retrofit;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepAPIFactory implements Constants {

    public static final String API_DOMAIN = "park.catchchatchina.com";
    public static final String API_ENDPOINT_FAYE = "https://faye.catchchatchina.com/faye";
    public static final String API_ENDPOINT_REST = "https://" + API_DOMAIN + "/api/";

    public static YepAPI getInstance(Context context, Account account) {
        if (account == null) return null;
        return getInstanceWithToken(context, getAuthToken(context, account));
    }

    public static YepAPI getInstanceWithToken(final Context context, final String accessToken) {
        Retrofit.Builder builder = new Retrofit.Builder();
        final OkHttpClient client = getOkHttpClient(context);
        builder.client(client);
        builder.baseUrl(API_ENDPOINT_REST);
        builder.addCallAdapterFactory(new YepCallAdapterFactory());
        builder.addConverterFactory(new LoganSquareConverterFactory());

        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("Accept", "application/json");
                if (accessToken != null) {
                    builder.addHeader("Authorization", getAuthorizationHeaderValue(accessToken));
                }
                return chain.proceed(builder.build());
            }
        });
        final Retrofit retrofit = builder.build();
        return retrofit.create(YepAPI.class);
    }

    public static OkHttpClient getOkHttpClient(Context context) {
        final OkHttpClient client = new OkHttpClient();
        DebugModeUtils.initForHttpClient(client);
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
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
