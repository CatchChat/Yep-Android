package catchla.yep.util;

import android.support.annotation.NonNull;

import org.mariotaku.mediaviewer.library.CacheDownloadLoader;
import org.mariotaku.mediaviewer.library.MediaDownloader;

import java.io.IOException;
import java.io.InputStream;

import catchla.yep.model.CacheMetadata;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
        if (!response.isSuccessful()) {
            response.body().close();
            throw new IOException("Unable to get " + url + ": " + response.code());
        }
        final ResponseBody body = response.body();
        CacheMetadata metadata = new CacheMetadata();
        metadata.setContentType(body.contentType().toString());
        return new OkDownloadResult(body, metadata);
    }


    private static class OkDownloadResult implements CacheDownloadLoader.DownloadResult {
        private final ResponseBody mBody;
        private final CacheMetadata mMetadata;

        public OkDownloadResult(ResponseBody body, CacheMetadata metadata) {
            mBody = body;
            mMetadata = metadata;
        }

        @Override
        public void close() throws IOException {
            mBody.close();
        }

        @Override
        public long getLength() throws IOException {
            return mBody.contentLength();
        }

        @Override
        public InputStream getStream() throws IOException {
            return mBody.byteStream();
        }

        @Override
        public byte[] getExtra() {
            if (mMetadata == null) return null;
            final String serialize = JsonSerializer.serialize(mMetadata, CacheMetadata.class);
            if (serialize == null) return null;
            return serialize.getBytes();
        }

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }
    }
}
