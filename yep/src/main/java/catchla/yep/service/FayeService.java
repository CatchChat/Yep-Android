package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import catchla.yep.Constants;
import catchla.yep.model.Message;
import catchla.yep.util.FayeClient;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPIFactory;

public class FayeService extends Service implements Constants {

    private FayeClient mFayeClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        final OkHttpClient client = YepAPIFactory.getOkHttpClient(this);
        final Request.Builder builder = new Request.Builder();
        builder.url(YepAPIFactory.API_ENDPOINT_FAYE);
        final String authToken = YepAPIFactory.getAuthToken(this, account);
        final String accountId = Utils.getAccountId(this, account);

        mFayeClient = FayeClient.create(client, builder.build());
        mFayeClient.addExtension(new FayeClient.FayeExtension() {

            @Override
            public void processIncoming(final FayeClient.Message message) {
            }

            @Override
            public void processOutgoing(final FayeClient.Message message) {
                JSONObject ext = new JSONObject();
                try {
                    ext.put("version", "v1");
                    ext.put("access_token", authToken);
                    message.put("ext", ext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        final String userChannel = String.format(Locale.ROOT, "/v1/users/%s/messages", accountId);
        mFayeClient.establish(new FayeClient.ConnectionListener() {
            @Override
            public void onConnected() {
                Log.d(LOGTAG, "Connected");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mFayeClient.isConnected()) {
                            mFayeClient.ping();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(final IOException e) {
                Log.w(LOGTAG, e);
            }

            @Override
            public void onClose(final int code, final String reason) {
                Log.d(LOGTAG, "Closed " + code);
            }
        }).handshake(new FayeClient.Callback() {
            @Override
            public void callback(final FayeClient.Message message) {
            }
        }).subscribe(userChannel, new FayeClient.Callback() {
            @Override
            public void callback(final FayeClient.Message message) {
                final JSONObject data = message.getJSONObject("data");
                final String msgType = data.optString("message_type");
                if ("message".equals(msgType)) {
                    Message imMessage = JsonSerializer.parse(data.optJSONObject("message").toString(),
                            Message.class);
                    MessageService.insertMessages(FayeService.this, Collections.singleton(imMessage), accountId);
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mFayeClient != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mFayeClient.disconnect();
                }
            }).start();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        throw new UnsupportedOperationException();
    }


}