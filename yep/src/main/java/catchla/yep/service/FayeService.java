package catchla.yep.service;

import android.accounts.Account;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.saulpower.fayeclient.FayeClient;
import com.saulpower.fayeclient.WebSocketClient;

import org.json.JSONObject;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPIFactory;

public class FayeService extends IntentService implements FayeClient.FayeListener {

    public final String TAG = this.getClass().getSimpleName();

    FayeClient mClient;
    private Handler mHandler;

    public FayeService() {
        super("FayeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Starting Web Socket");
        final Account account = Utils.getCurrentAccount(this);
        final User accountUser = Utils.getAccountUser(this, account);
        if (account == null || accountUser == null) return;

        final URI uri = URI.create("wss://faye.catchchatchina.com/faye");
        final String channel = String.format("/v1/users/%s/messages", accountUser.getId());


        WebSocketClient.setTrustManagers(new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }});
        mClient = new FayeClient(mHandler, uri, channel);
        mClient.setFayeListener(this);
        mClient.connectToServer(YepAPIFactory.getFayeAuthExtension(this, account));

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void connectedToServer() {
        Log.i(TAG, "Connected to Server");
    }

    @Override
    public void disconnectedFromServer() {
        Log.i(TAG, "Disonnected to Server");
    }

    @Override
    public void subscribedToChannel(String subscription) {
        Log.i(TAG, String.format("Subscribed to channel %s on Faye", subscription));
    }

    @Override
    public void subscriptionFailedWithError(String error) {
        Log.i(TAG, String.format("Subscription failed with error: %s", error));
    }

    @Override
    public void messageReceived(JSONObject json) {
        Log.i(TAG, String.format("Received message %s", json.toString()));
    }
}