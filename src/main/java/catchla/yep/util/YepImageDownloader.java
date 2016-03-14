package catchla.yep.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.apache.commons.lang3.math.NumberUtils;
import org.mariotaku.mediaviewer.library.MediaDownloader;

import java.io.FileNotFoundException;
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

    @Override
    protected InputStream getStreamFromOtherSource(final String imageUri, final Object extra) throws IOException {
        Uri uri = Uri.parse(imageUri);
        if ("media-thumb".equals(uri.getScheme())) {
            final long id = NumberUtils.toLong(uri.getHost(), -1);
            if (id == -1) throw new FileNotFoundException(imageUri);
            final String[] projection = {MediaStore.Images.Thumbnails.DATA};
            final Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MINI_KIND, projection);
            if (cursor == null) throw new FileNotFoundException(imageUri);
            try {
                if (cursor.moveToFirst()) {
                    return getStreamFromFile("file://" + cursor.getString(0), extra);
                }
            } finally {
                cursor.close();
            }
        }
        return super.getStreamFromOtherSource(imageUri, extra);
    }
}
