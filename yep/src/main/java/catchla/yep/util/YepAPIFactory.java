package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.net.Uri;
import android.util.Pair;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.mariotaku.restfu.ExceptionFactory;
import org.mariotaku.restfu.RequestInfoFactory;
import org.mariotaku.restfu.RestAPIFactory;
import org.mariotaku.restfu.RestClient;
import org.mariotaku.restfu.RestMethodInfo;
import org.mariotaku.restfu.RestRequestInfo;
import org.mariotaku.restfu.annotation.RestMethod;
import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.restfu.http.FileValue;
import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import catchla.yep.Constants;
import catchla.yep.model.TokenAuthorization;
import catchla.yep.util.net.OkHttpRestClient;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepAPIFactory implements Constants {

    public static final String API_DOMAIN = "park-staging.catchchatchina.com";

    public static YepAPI getInstance(Context context, Account account) {
        if (account == null) return null;
        return getInstanceWithToken(context, getAuthToken(context, account));
    }

    public static YepAPI getInstanceWithToken(final Context context, final String accessToken) {
        RestAPIFactory factory = new RestAPIFactory();
        factory.setEndpoint(new Endpoint("https://" + API_DOMAIN + "/api/"));
        final OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        client.setSslSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
        factory.setClient(new OkHttpRestClient(context, client));
        factory.setConverter(new LoganSquareConverter());
        factory.setAuthorization(new TokenAuthorization(accessToken));
        factory.setRequestInfoFactory(new RequestInfoFactory() {
            @Override
            public RestRequestInfo create(final RestMethodInfo methodInfo) {

                final RestMethod method = methodInfo.getMethod();
                final String path = methodInfo.getPath();
                final List<Pair<String, String>> queries = new ArrayList<>(methodInfo.getQueries());
                final List<Pair<String, String>> forms = new ArrayList<>(methodInfo.getForms());
                final List<Pair<String, String>> headers = methodInfo.getHeaders();
                final List<Pair<String, TypedData>> parts = methodInfo.getParts();
                final FileValue file = methodInfo.getFile();
                final Map<String, Object> extras = methodInfo.getExtras();
                headers.add(Pair.create("Accept", "application/json"));
                return new RestRequestInfo(method.value(), path, queries, forms, headers, parts, file,
                        methodInfo.getBody(), extras);
            }
        });
        factory.setExceptionFactory(new ExceptionFactory() {
            @Override
            public Exception newException(final Throwable cause, final RestHttpRequest request, final RestHttpResponse response) {
                YepException exception;
                try {
                    if (response != null) {
                        exception = LoganSquare.parse(response.getBody().stream(), YepException.class);
                    } else {
                        exception = new YepException(cause);
                    }
                } catch (IOException e) {
                    exception = new YepException(cause);
                }
                exception.setRequest(request);
                exception.setResponse(response);
                return exception;
            }
        });
        return factory.build(YepAPI.class);
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
        return ("https://" + API_DOMAIN + "/auth/success").equals(url);
    }

    public static boolean isAuthFailureUrl(final String url) {
        return ("https://" + API_DOMAIN + "/auth/failure").equals(url);
    }

    public static RestHttpClient getHttpClient(final Object o) {
        final InvocationHandler handler = Proxy.getInvocationHandler(o);
        final RestClient client = (RestClient) handler;
        return client.getRestClient();
    }

    public static JSONObject getFayeAuthExtension(final Context context, final Account account) {
        try {
            final JSONObject ext = new JSONObject();
            ext.put("version", "v1");
            ext.put("access_token", getAuthToken(context, account));
            return ext;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
