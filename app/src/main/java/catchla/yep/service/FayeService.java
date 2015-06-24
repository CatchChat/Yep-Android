package catchla.yep.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.saulpower.fayeclient.FayeClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class FayeService extends IntentService implements FayeClient.FayeListener {

    public final String TAG = this.getClass().getSimpleName();

    FayeClient mClient;
    private Handler mHandler;

    public FayeService() {
        super("WebSocketService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Starting Web Socket");

        try {

            String baseUrl = "";

            URI uri = URI.create(String.format("wss://%s:443/events", baseUrl));
            String channel = String.format("/%s/**", "");

            JSONObject ext = new JSONObject();
            ext.put("version", "v1");
            ext.put("auth_token", "");

            mClient = new FayeClient(mHandler, uri, channel);
            mClient.setFayeListener(this);
            mClient.connectToServer(ext);

        } catch (JSONException ex) {
        }
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