package catchla.yep.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.view.Menu
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import catchla.yep.BuildConfig
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.model.Provider
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.activity_provider_oauth.*
import java.util.*

/**
 * Created by mariotaku on 15/6/3.
 */
class ProviderOAuthActivity : ContentActivity(), Constants {
    private var loadProgress: View? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_oauth)
        val providerName = intent.getStringExtra(Constants.EXTRA_PROVIDER_NAME)
        val url = YepAPIFactory.getProviderOAuthUrl(providerName)
        val headers = HashMap<String, String>()
        headers.put("Authorization", YepAPIFactory.getAuthorizationHeaderValue(
                YepAPIFactory.getAuthToken(this, account)!!))
        title = getString(R.string.sign_in_to_provider_name, Provider.getProviderName(this, providerName))
        webView.loadUrl(url, headers)
        webView.setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                if (YepAPIFactory.isAuthSuccessUrl(url)) {
                    finish()
                } else if (YepAPIFactory.isAuthFailureUrl(url)) {
                    Toast.makeText(this@ProviderOAuthActivity, R.string.unable_to_connect, Toast.LENGTH_SHORT).show()
                    finish()
                } else if (YepAPIFactory.isAPIUrl(uri)) {
                    view.loadUrl(url, headers)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                loadProgress?.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadProgress?.visibility = View.VISIBLE
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                if (BuildConfig.DEBUG) {
                    handler.proceed()
                } else {
                    handler.cancel()
                }
            }
        })
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.blockNetworkLoads = false
        settings.blockNetworkImage = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_provider_oauth, menu)
        loadProgress = MenuItemCompat.getActionView(menu.findItem(R.id.load_progress))
        return true
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
