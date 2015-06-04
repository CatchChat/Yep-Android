package catchla.yep.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Provider;
import catchla.yep.model.TokenAuthorization;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/6/3.
 */
public class ProviderOAuthActivity extends ContentActivity implements Constants {
    private WebView mWebView;
    private View mLoadProgress;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_oauth);
        final Intent intent = getIntent();
        final String providerName = intent.getStringExtra(EXTRA_PROVIDER_NAME);
        final String url = YepAPIFactory.getProviderOAuthUrl(providerName);
        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", TokenAuthorization.getAuthorizationHeaderValue(
                YepAPIFactory.getAuthToken(this, Utils.getCurrentAccount(this))));
        setTitle(getString(R.string.sign_in_to_provider_name, Provider.getProviderName(this, providerName)));
        mWebView.loadUrl(url, headers);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                final Uri uri = Uri.parse(url);
                if (YepAPIFactory.isAPIUrl(uri)) {
                    if (YepAPIFactory.isAuthSuccessUrl(url)) {
                        finish();
                    } else if (YepAPIFactory.isAuthFailureUrl(url)) {
                        // TODO: Show error message
                    } else {
                        view.loadUrl(url, headers);
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                if (mLoadProgress != null) {
                    mLoadProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mLoadProgress != null) {
                    mLoadProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedSslError(final WebView view, @NonNull final SslErrorHandler handler, final SslError error) {
                if (BuildConfig.DEBUG) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
            }
        });
        final WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkLoads(false);
        settings.setBlockNetworkImage(false);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_provider_oauth, menu);
        mLoadProgress = MenuItemCompat.getActionView(menu.findItem(R.id.load_progress));
        return true;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mWebView = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}
