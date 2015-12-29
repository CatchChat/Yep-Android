package catchla.yep.util;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.jr.tree.JacksonJrSimpleTreeCodec;
import com.fasterxml.jackson.jr.tree.JacksonJrValue;
import com.fasterxml.jackson.jr.tree.JsonArray;
import com.fasterxml.jackson.jr.tree.JsonBoolean;
import com.fasterxml.jackson.jr.tree.JsonNumber;
import com.fasterxml.jackson.jr.tree.JsonString;
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import okio.Buffer;

/**
 * Created by mariotaku on 15/11/11.
 */
public class FayeClient implements Constants {
    private final WebSocketCall call;
    private final List<FayeExtension> extensions = new ArrayList<>();
    private final HashMap<String, Callback> callbacks = new HashMap<>();
    private final HashMap<String, Callback> subscriptions = new HashMap<>();
    private WebSocket webSocket;
    private long id;
    private String clientId;
    private boolean wantClose;

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
                wantClose = false;
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
        if (call != null) {
            call.cancel();
        }
        if (webSocket == null) return;
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
        try {
            webSocket.sendMessage(body);
        } catch (IOException e) {
            webSocket.close(1000, "OK");
            wantClose = true;
            throw e;
        } catch (IllegalStateException e) {
            if (BuildConfig.DEBUG) Log.w(LOGTAG, e);
        }
    }

    private void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public void ping() {
        if (wantClose) return;
        Buffer buffer = new Buffer();
        buffer.writeString("[]", Charset.defaultCharset());
        try {
            webSocket.sendPing(buffer);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.w(LOGTAG, e);
        } catch (IllegalStateException e) {
            if (BuildConfig.DEBUG) Log.w(LOGTAG, e);
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }

    public void publish(String channel, final Message message, final Callback callback) throws IOException {
        message.setChannel(channel);
        emit(new Message[]{message}, new Callback() {
            @Override
            public void callback(final Message message) {
                if (callback != null) {
                    callback.callback(message);
                }
            }
        });
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
        @WorkerThread
        void callback(Message message);
    }

    public static class Message {

        private final Map<String, TreeNode> json;

        public Message() {
            this(new LinkedHashMap<String, TreeNode>());
        }

        public Message(final TreeNode json) {
            this(toMap(json));
        }

        private static Map<String, TreeNode> toMap(final TreeNode json) {
            final LinkedHashMap<String, TreeNode> map = new LinkedHashMap<>();
            final Iterator<String> fieldNames = json.fieldNames();
            while (fieldNames.hasNext()) {
                final String fieldName = fieldNames.next();
                map.put(fieldName, json.get(fieldName));
            }
            return map;
        }

        public Message(final Map<String, TreeNode> json) {
            this.json = json;
        }

        public static <T> T getAs(TreeNode node, Class<T> cls) {
            try {
                return LoganSquare.mapperFor(cls).parse(JacksonJrSimpleTreeCodec.SINGLETON.treeAsTokens(node));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static Message[] parse(String str) throws IOException {
            TreeNode json = JacksonJrSimpleTreeCodec.SINGLETON.readTree(LoganSquare.JSON_FACTORY.createParser(str));
            final Message[] message = new Message[json.size()];
            for (int i = 0; i < message.length; i++) {
                message[i] = new Message(json.get(i));
            }
            return message;
        }

        public void put(String key, String value) {
            json.put(key, new JsonString(value));
        }

        public void put(String key, TreeNode value) {
            json.put(key, value);
        }


        public String getId() {
            return getString("id");
        }

        public boolean isSuccessful() {
            return getJson("successful") == JsonBoolean.TRUE;
        }

        public TreeNode getJson(String key) {
            return json.get(key);
        }

        void setId(final String id) {
            put("id", id);
        }

        public String getChannel() {
            return getString("channel");
        }

        private String getString(String name) {
            return ((JsonString) json.get(name)).getValue();
        }

        void setChannel(final String id) {
            put("channel", id);
        }

        @Override
        public String toString() {
            return "Message{" +
                    "json=" + json +
                    '}';
        }

        void setVersion(final String version) {
            put("version", version);
        }

        void setSupportedConnectionTypes(final String[] supportedConnectionTypes) {
            final List<JacksonJrValue> values = new LinkedList<>();
            for (final String supportedConnectionType : supportedConnectionTypes) {
                values.add(new JsonString(supportedConnectionType));
            }
            put("supportedConnectionTypes", new JsonArray(values));
        }

        void setClientId(final String clientId) {
            put("clientId", clientId);
        }

        public static String toJson(final Message[] messages) {
            final JSONArray json = new JSONArray();
            for (final Message message : messages) {
                json.put(message.json);
            }
            return json.toString();
        }

        public Advice getAdvice() {
            return new Advice(json.get("advice"));
        }


        void setConnectionType(final String type) {
            put("connectionType", type);
        }

        public class Advice {

            String reconnect;
            long interval;
            long timeout;

            public Advice(final TreeNode json) {
                if (json == null) return;
                if (!json.path("reconnect").isMissingNode()) {
                    reconnect = ((JsonString) json.path("reconnect")).getValue();
                }
                if (!json.path("interval").isMissingNode()) {
                    interval = ((JsonNumber) json.path("interval")).getValue().longValue();
                }
                if (!json.path("timeout").isMissingNode()) {
                    timeout = ((JsonNumber) json.path("timeout")).getValue().longValue();
                }
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
