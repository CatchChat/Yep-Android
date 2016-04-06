package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.squareup.otto.Bus;

import org.mariotaku.okfaye.Extension;
import org.mariotaku.okfaye.Faye;
import org.mariotaku.sqliteqb.library.Expression;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.IFayeService;
import catchla.yep.model.Circle;
import catchla.yep.model.Conversation;
import catchla.yep.model.ConversationsResponse;
import catchla.yep.model.InstantStateMessage;
import catchla.yep.model.MarkAsReadMessage;
import catchla.yep.model.Message;
import catchla.yep.model.MessageType;
import catchla.yep.model.User;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.dagger.GeneralComponentHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ws.WebSocketCall;

public class FayeService extends Service implements Constants {

    @Inject
    Bus mBus;
    Executor mExecutor = Executors.newSingleThreadExecutor();

    private Faye mFayeClient;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        GeneralComponentHelper.build(this).inject(this);
    }

    @JsonObject
    public static class YepFayeExtension extends Extension {
        @JsonField(name = "version")
        String version;
        @JsonField(name = "access_token")
        String accessToken;

        public void setVersion(final String version) {
            this.version = version;
        }

        public void setAccessToken(final String accessToken) {
            this.accessToken = accessToken;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        final OkHttpClient client = YepAPIFactory.getOkHttpClient(this);
        final Request.Builder builder = new Request.Builder();
        builder.url(BuildConfig.API_ENDPOINT_FAYE);
        final String authToken = YepAPIFactory.getAuthToken(this, account);
        final String accountId = Utils.getAccountId(this, account);

        mFayeClient = Faye.create(client, WebSocketCall.create(client, builder.build()));
        YepFayeExtension extension = new YepFayeExtension();
        extension.setVersion("v1");
        extension.setAccessToken(authToken);
        mFayeClient.setExtension(extension);
        mFayeClient.setErrorListener(new Faye.ErrorListener() {
            @Override
            public void error(final IOException e, final int code, final String message) {
                if (e != null) {
                    Log.w(LOGTAG, String.format(Locale.ROOT, "%d: %s", code, message), e);
                } else {
                    Log.w(LOGTAG, String.format(Locale.ROOT, "%d: %s", code, message));
                }
            }
        });
        final String userChannel = String.format(Locale.ROOT, "/v1/users/%s/messages", accountId);
        mFayeClient.subscribe(userChannel, new Faye.Callback<String>() {

            @Override
            public void callback(final String json) {
                Log.d(LOGTAG, String.valueOf(json));
                consumeMessage(json, MessageType.class, new MessageConsumer<MessageType>() {
                    @Override
                    public void consume(@NonNull final MessageType messageType) {
                        switch (messageType.getMessageType()) {
                            case "message": {
                                consumeMessage(json, ImMessage.class, new MessageConsumer<ImMessage>() {
                                    @Override
                                    public void consume(@NonNull final ImMessage message) {
                                        ConversationsResponse response = new ConversationsResponse();
                                        final User sender = message.message.getSender();
                                        if (sender != null) {
                                            response.setUsers(Collections.singletonList(sender));
                                        }
                                        final Circle circle = message.message.getCircle();
                                        if (circle != null) {
                                            response.setCircles(Collections.singletonList(circle));
                                        }
                                        response.setMessages(Collections.singletonList(message.message));
                                        MessageService.insertConversations(FayeService.this, response, accountId);
                                    }
                                });
                                break;
                            }
                            case "mark_as_read": {
                                consumeMessage(json, MarkAsRead.class, new MessageConsumer<MarkAsRead>() {
                                    @Override
                                    public void consume(@NonNull final MarkAsRead parsed) {
                                        final MarkAsReadMessage markAsRead = parsed.markAsRead;
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
                                });
                                break;
                            }
                            case "instant_state": {
                                consumeMessage(json, InstantState.class, new MessageConsumer<InstantState>() {
                                    @Override
                                    public void consume(@NonNull final InstantState parsed) {
                                        postMessage(parsed.instantState);
                                    }
                                });
                                break;
                            }
                        }
                    }
                });
            }
        });
        return START_REDELIVER_INTENT;
    }

    private static <T> void consumeMessage(@Nullable String json, @NonNull Class<T> type,
                                           @NonNull MessageConsumer<T> consumer) {
        final T parsed = JsonSerializer.parse(json, type);
        if (parsed == null) return;
        consumer.consume(parsed);
    }

    interface MessageConsumer<T> {
        void consume(@NonNull T parsed);
    }

    @Override
    public void onDestroy() {
        if (mFayeClient != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mFayeClient.disconnect();
                }
            });
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
        if (mFayeClient == null || mFayeClient.getState() != Faye.CONNECTED) return false;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                FayeSend fayeSend = new FayeSend();
                fayeSend.messageType = messageType;
                fayeSend.message = message;
                mFayeClient.publish(channel, JsonSerializer.serialize(fayeSend), null);
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

    @JsonObject
    static class ImMessage {
        @JsonField(name = "message")
        Message message;
    }

    @JsonObject
    static class MarkAsRead {
        @JsonField(name = "message")
        MarkAsReadMessage markAsRead;
    }

    @JsonObject
    static class InstantState {
        @JsonField(name = "message")
        InstantStateMessage instantState;
    }

    @JsonObject
    static class FayeSend {
        @JsonField(name = "message_type")
        String messageType;
        @JsonField(name = "message", typeConverter = MessageSerializer.class)
        Object message;
    }


    static class MessageSerializer implements TypeConverter<Object> {
        MessageSerializer() {
        }

        public Object parse(JsonParser jsonParser) throws IOException {
            return null;
        }

        public void serialize(Object object, String fieldName, boolean writeFieldNameForObject,
                              JsonGenerator jsonGenerator) throws IOException {
            if (object == null) return;
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            //noinspection unchecked
            JsonMapper<Object> mapper = (JsonMapper<Object>) LoganSquare.mapperFor(object.getClass());
            mapper.serialize(object, jsonGenerator, true);
        }
    }
}