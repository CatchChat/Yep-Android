package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.util.YepAPIFactory;
import okio.Buffer;

public class FayeService extends Service implements Constants, WebSocketListener {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
        final OkHttpClient client = YepAPIFactory.getOkHttpClient(this);
        final Request.Builder builder = new Request.Builder();
        final WebSocketCall call = WebSocketCall.create(client, builder.build());
        call.enqueue(this);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onOpen(final WebSocket webSocket, final Response response) {

    }

    @Override
    public void onFailure(final IOException e, final Response response) {

    }

    @Override
    public void onMessage(final ResponseBody message) throws IOException {

    }

    @Override
    public void onPong(final Buffer payload) {

    }

    @Override
    public void onClose(final int code, final String reason) {

    }

}