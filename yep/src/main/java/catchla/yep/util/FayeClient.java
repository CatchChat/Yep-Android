package catchla.yep.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import org.jdeferred.DoneCallback;
import org.jdeferred.impl.DeferredObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okio.Buffer;

/**
 * Created by mariotaku on 15/11/11.
 */
public class FayeClient {
    private final WebSocketCall call;
    private final List<FayeExtension> extensions = new ArrayList<>();
    private final HashMap<String, Callback> callbacks = new HashMap<>();
    private final HashMap<String, Callback> subscriptions = new HashMap<>();
    private WebSocket webSocket;
    private long id;
    private String clientId;

    public FayeClient(final WebSocketCall call) {
        this.call = call;
    }

    public static FayeClient create(final OkHttpClient client, final Request request) {
        client.setReadTimeout(0, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(0, TimeUnit.MILLISECONDS);
        return new FayeClient(WebSocketCall.create(client, request));
    }

    public Established establish(final ConnectionListener listener) {
        final Established established = new Established();
        call.enqueue(new WebSocketListener() {
            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                setWebSocket(webSocket);
                listener.onConnected();
                established.resolve(webSocket);
            }

            @Override
            public void onFailure(final IOException e, final Response response) {
                webSocket = null;
                listener.onFailure(e);
            }

            @Override
            public void onMessage(final ResponseBody message) throws IOException {
                final Message[] parsed = Message.parse(message.string());
                for (final Message item : parsed) {
                    for (FayeExtension extension : extensions) {
                        extension.processIncoming(item);
                    }
                    final Callback callback = callbacks.remove(item.getId());
                    if (callback != null) {
                        callback.callback(item);
                    }
                    final String channel = item.getChannel();
                    final Callback channelCallback = subscriptions.get(channel);
                    if (channelCallback != null) {
                        channelCallback.callback(item);
                    }
                    Message.Advice advice = item.getAdvice();
                    if (advice != null && "retry".equals(advice.reconnect)) {
                        connect(null);
                    }
                }
            }

            @Override
            public void onPong(final Buffer payload) {
            }

            @Override
            public void onClose(final int code, final String reason) {
                listener.onClose(code, reason);
            }
        });
        return established;
    }

    private void connect(final Callback callback) throws IOException {
        final Message message = new Message();
        message.setChannel("/meta/connect");
        message.setConnectionType("websocket");
        emit(new Message[]{message}, callback);
    }

    void setWebSocket(final WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void disconnect() {
        call.cancel();
        try {
            webSocket.close(1000, "Exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
        webSocket = null;
    }

    public void addExtension(FayeExtension extension) {
        extensions.add(extension);
    }

    public void removeExtension(FayeExtension extension) {
        extensions.remove(extension);
    }

    public void subscribe(String channel, Callback listener) {

    }

    public void unsubscribe(String channel, Callback listener) {

    }

    private void emit(Message[] messages, final Callback callback) throws IOException {
        for (final Message message : messages) {
            final String idStr = Long.toHexString(++this.id);
            message.setId(idStr);
            if (clientId != null) {
                message.setClientId(clientId);
            }
            for (FayeExtension extension : extensions) {
                extension.processOutgoing(message);
            }
            callbacks.put(idStr, callback);
        }
        final RequestBody body = RequestBody.create(WebSocket.TEXT, Message.toJson(messages));
        webSocket.sendMessage(body);
    }

    private void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void ping() {
        Buffer buffer = new Buffer();
        buffer.writeString("[]", Charset.defaultCharset());
        try {
            webSocket.sendPing(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }

    public interface ConnectionListener {
        void onConnected();

        void onFailure(IOException e);

        void onClose(int code, String reason);
    }

    public interface FayeExtension {
        void processIncoming(Message json);

        void processOutgoing(Message json);
    }

    public interface Callback {
        void callback(Message message);
    }

    public static class Message {
        private final JSONObject json;

        public Message() {
            this(new JSONObject());
        }

        public Message(final JSONObject json) {
            this.json = json;
        }

        public static Message[] parse(String str) throws IOException {
            try {
                final JSONArray json = new JSONArray(str);
                final Message[] message = new Message[json.length()];
                for (int i = 0; i < message.length; i++) {
                    message[i] = new Message(json.getJSONObject(i));
                }
                return message;
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        public void put(String key, String value) {
            putInternal(key, value);
        }

        public void put(String key, JSONObject value) {
            putInternal(key, value);
        }

        private void putInternal(final String key, final Object value) {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                // Ignore
            }
        }

        public String getId() {
            try {
                return json.getString("id");
            } catch (JSONException e) {
                return null;
            }
        }

        public String getString(String key) {
            return json.optString(key, null);
        }

        public JSONObject getJSONObject(String key) {
            return json.optJSONObject(key);
        }

        void setId(final String id) {
            putInternal("id", id);
        }

        public String getChannel() {
            try {
                return json.getString("channel");
            } catch (JSONException e) {
                return null;
            }
        }

        void setChannel(final String id) {
            putInternal("channel", id);
        }

        public String toJson() {
            final JSONArray array = new JSONArray();
            array.put(json);
            return array.toString();
        }

        void setVersion(final String version) {
            putInternal("version", version);
        }

        void setSupportedConnectionTypes(final String[] supportedConnectionTypes) {
            final JSONArray typesJson = new JSONArray();
            for (final String supportedConnectionType : supportedConnectionTypes) {
                typesJson.put(supportedConnectionType);
            }
            putInternal("supportedConnectionTypes", typesJson);
        }

        void setClientId(final String clientId) {
            putInternal("clientId", clientId);
        }

        public static String toJson(final Message[] messages) {
            final JSONArray json = new JSONArray();
            for (final Message message : messages) {
                json.put(message.json);
            }
            return json.toString();
        }

        public Advice getAdvice() {
            try {
                return new Advice(json.getJSONObject("advice"));
            } catch (JSONException e) {
                return null;
            }
        }


        void setConnectionType(final String type) {
            putInternal("connectionType", type);
        }

        public class Advice {

            String reconnect;
            long interval;
            long timeout;

            public Advice(final JSONObject json) {
                reconnect = json.optString("reconnect");
                interval = json.optLong("interval");
                timeout = json.optLong("timeout");
            }
        }
    }

    public class Established extends DeferredObject<WebSocket, Object, Object> {

        public Handshake handshake() {
            return handshake(null);
        }

        public Handshake handshake(final Callback callback) {
            final Handshake handshake = new Handshake();
            done(new DoneCallback<WebSocket>() {
                @Override
                public void onDone(final WebSocket webSocket) {
                    final Message request = new Message();
                    request.setChannel("/meta/handshake");
                    request.setVersion("1.0");
                    final String[] supported = {"websocket", "eventsource", "long-polling", "cross-origin-long-polling", "callback-polling"};
                    request.setSupportedConnectionTypes(supported);
                    try {
                        emit(new Message[]{request}, new Callback() {
                            @Override
                            public void callback(final Message message) {
                                if (callback != null) {
                                    callback.callback(message);
                                }
                                setClientId(message.getString("clientId"));
                                handshake.resolve(message);
                            }
                        });
                    } catch (IOException e) {
                        triggerFail(e);
                    }
                }
            });
            return handshake;
        }
    }

    public class Handshake extends DeferredObject<Object, Object, Object> {

        public Subscription subscribe(String channel, Callback listener) {
            return subscribe(channel, null, listener);
        }

        public Subscription subscribe(final String channel, final Callback callback, final Callback listener) {
            subscriptions.put(channel, listener);
            final Subscription subscription = new Subscription();
            done(new DoneCallback<Object>() {
                @Override
                public void onDone(final Object webSocket) {
                    final Message request = new Message();
                    request.setChannel("/meta/subscribe");
                    request.put("subscription", channel);
                    try {
                        emit(new Message[]{request}, new Callback() {
                            @Override
                            public void callback(final Message message) {
                                if (callback != null) {
                                    callback.callback(message);
                                }
                                subscription.resolve(message);
                            }
                        });
                    } catch (IOException e) {
                        triggerFail(e);
                    }
                }
            });
            return subscription;
        }
    }

    public class Subscription extends DeferredObject<Object, Object, Object> {

    }
}
