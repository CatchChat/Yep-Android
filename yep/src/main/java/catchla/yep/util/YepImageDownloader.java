package catchla.yep.util;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.io.InputStream;

import catchla.yep.app.YepApplication;

/**
 * Created by mariotaku on 15/10/15.
 */
public class YepImageDownloader extends BaseImageDownloader {
    private final OkHttpClient client;

    @Override
    protected InputStream getStreamFromNetwork(final String imageUri, final Object extra) throws IOException {
        final Request.Builder builder = new Request.Builder();
        builder.url(imageUri);
        final Request request = builder.build();
        return client.newCall(request).execute().body().byteStream();
    }

    public YepImageDownloader(final YepApplication application) {
        super(application);
        this.client = YepAPIFactory.getOkHttpClient(application);
    }


}
