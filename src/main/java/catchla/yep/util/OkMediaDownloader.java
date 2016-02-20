package catchla.yep.util;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.mariotaku.mediaviewer.library.CacheDownloadLoader;
import org.mariotaku.mediaviewer.library.MediaDownloader;

import java.io.IOException;

/**
 * Created by mariotaku on 16/1/20.
 */
public class OkMediaDownloader implements MediaDownloader {
    private final OkHttpClient client;

    public OkMediaDownloader(final OkHttpClient client) {
        this.client = client;
    }

    @NonNull
    @Override
    public CacheDownloadLoader.DownloadResult get(@NonNull final String url, final Object extra) throws IOException {
        final Request.Builder builder = new Request.Builder();
        builder.url(url);
        Response response = client.newCall(builder.build()).execute();
        final ResponseBody body = response.body();
        return new CacheDownloadLoader.DownloadResult(body.contentLength(), body.byteStream());
    }
}
