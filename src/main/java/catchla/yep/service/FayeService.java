package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.jr.tree.JacksonJrSimpleTreeCodec;
import com.fasterxml.jackson.jr.tree.JacksonJrValue;
import com.fasterxml.jackson.jr.tree.JsonObject;
import com.fasterxml.jackson.jr.tree.JsonString;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.otto.Bus;

import org.mariotaku.sqliteqb.library.Expression;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.IFayeService;
import catchla.yep.model.Conversation;
import catchla.yep.model.InstantStateMessage;
import catchla.yep.model.MarkAsReadMessage;
import catchla.yep.model.Message;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.FayeClient;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.dagger.GeneralComponentHelper;

public class FayeService extends Service implements Constants {

    @Inject
    Bus mBus;
    private FayeClient mFayeClient;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        GeneralComponentHelper.build(this).inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
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
                LinkedHashMap<String, JacksonJrValue> nodes = new LinkedHashMap<>();
                nodes.put("version", new JsonString("v1"));
                nodes.put("access_token", new JsonString(authToken));
                message.put("ext", new JsonObject(nodes));
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
                if (!message.isSuccessful()) {
                    Log.w(LOGTAG, "Received error from faye, " + message.toString());
                    return;
                }
                final TreeNode data = message.getJson("data");
                final String msgType = ((JsonString) data.get("message_type")).getValue();
                switch (msgType) {
                    case "message": {
                        Message imMessage = FayeClient.Message.getAs(data.get("message"), Message.class);
                        MessageService.insertMessages(FayeService.this, Collections.singleton(imMessage), accountId);
                        break;
                    }
                    case "mark_as_read": {
                        MarkAsReadMessage markAsRead = FayeClient.Message.getAs(data.get("message"), MarkAsReadMessage.class);
                        if (markAsRead != null) {
                            final ContentValues values = new ContentValues();
                            values.put(Messages.STATE, Messages.MessageState.READ);
                            final Expression where = Expression.and(
                                    Expression.equalsArgs(Messages.RECIPIENT_ID),
                                    Expression.equalsArgs(Messages.RECIPIENT_TYPE),
                                    Expression.lesserEquals(Messages.CREATED_AT, markAsRead.getLastReadAt().getTime())
                            );
                            final String[] whereArgs = {markAsRead.getRecipientId(), markAsRead.getRecipientType()};
                            getContentResolver().update(Messages.CONTENT_URI, values, where.getSQL(), whereArgs);
                        }
                        break;
                    }
                    case "instant_state": {
                        final InstantStateMessage instantState = FayeClient.Message.getAs(data.get("message"),
                                InstantStateMessage.class);
                        if (instantState != null) {
                            postMessage(instantState);
                        }
                        break;
                    }
                }
            }
        });
        return START_REDELIVER_INTENT;
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
        return new IFayeServiceBinder(this);
    }

    private void postMessage(final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBus.post(obj);
            }
        });
    }

    private boolean sendMessage(final String messageType, final String channel, final Object message) {
        if (mFayeClient == null || !mFayeClient.isConnected()) return false;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Map<String, JacksonJrValue> data = new LinkedHashMap<>();
                    data.put("message_type", new JsonString(messageType));
                    final JsonMapper jsonMapper = LoganSquare.mapperFor(message.getClass());
                    //noinspection unchecked
                    final JsonParser parser = LoganSquare.JSON_FACTORY.createParser(jsonMapper.serialize(message));
                    data.put("message", (JacksonJrValue) JacksonJrSimpleTreeCodec.SINGLETON.readTree(parser));
                    mFayeClient.publish(channel, new FayeClient.Message(new JsonObject(Collections.
                            <String, JacksonJrValue>singletonMap("data", new JsonObject(data)))), null);
                } catch (IOException e) {
                    Log.w(LOGTAG, e);
                }
            }
        });
        return true;
    }

    private static class IFayeServiceBinder extends IFayeService.Stub {

        private final WeakReference<FayeService> mReference;

        IFayeServiceBinder(FayeService service) {
            mReference = new WeakReference<>(service);
        }

        @Override
        public boolean instantState(final Conversation conversation, final String type) throws RemoteException {
            final FayeService service = mReference.get();
            InstantStateMessage message = InstantStateMessage.create(type);
            final String userChannel = String.format(Locale.ROOT, "/users/%s/messages", conversation.getRecipientId());
            return service.sendMessage("instant_state", userChannel, message);
        }
    }

}