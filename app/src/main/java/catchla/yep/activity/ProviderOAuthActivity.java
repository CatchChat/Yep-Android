package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import catchla.yep.Constants;
import catchla.yep.R;

/**
 * Created by mariotaku on 15/6/3.
 */
public class ProviderOAuthActivity extends ContentActivity implements Constants {
    private WebView mWebView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_oauth);
        final Intent intent= getIntent();
        final String providerName = intent.getStringExtra(EXTRA_PROVIDER_NAME);

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
