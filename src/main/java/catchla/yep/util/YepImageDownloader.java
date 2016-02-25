package catchla.yep.util;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.mariotaku.mediaviewer.library.MediaDownloader;

import java.io.IOException;
import java.io.InputStream;

import catchla.yep.app.YepApplication;

/**
 * Created by mariotaku on 15/10/15.
 */
public class YepImageDownloader extends BaseImageDownloader {
    private final MediaDownloader downloader;

    @Override
    protected InputStream getStreamFromNetwork(final String imageUri, final Object extra) throws IOException {
        return downloader.get(imageUri, extra).getStream();
    }

    public YepImageDownloader(final YepApplication application, final MediaDownloader downloader) {
        super(application);
        this.downloader = downloader;
    }


}
