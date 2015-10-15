package catchla.yep.util;

import com.nostra13.universalimageloader.core.download.ImageDownloader;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.http.RestHttpRequest;

import java.io.IOException;
import java.io.InputStream;

import catchla.yep.app.YepApplication;
import catchla.yep.util.net.OkHttpRestClient;

/**
 * Created by mariotaku on 15/10/15.
 */
public class YepImageDownloader implements ImageDownloader {
    private final OkHttpRestClient client;

    public YepImageDownloader(final YepApplication application) {
        this.client = YepAPIFactory.getHttpRestClient(application);
    }

    @Override
    public InputStream getStream(final String imageUri, final Object extra) throws IOException {
        final RestHttpRequest.Builder builder = new RestHttpRequest.Builder();
        builder.url(imageUri);
        builder.method(GET.METHOD);
        final RestHttpRequest request = builder.build();
        return client.execute(request).getBody().stream();
    }
}
