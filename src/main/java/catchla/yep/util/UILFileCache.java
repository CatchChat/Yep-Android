package catchla.yep.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.utils.IoUtils;

import org.mariotaku.mediaviewer.library.FileCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import catchla.yep.provider.CacheProvider;

/**
 * Created by mariotaku on 16/1/20.
 */
public class UILFileCache implements FileCache {
    private final DiskCache cache;

    public UILFileCache(final DiskCache cache) {
        this.cache = cache;
    }

    @Override
    public File get(@NonNull final String key) {
        return cache.get(key);
    }

    @Override
    public void remove(@NonNull final String key) {
        cache.remove(key);
    }

    @Override
    public void save(@NonNull final String key, @NonNull final InputStream is, final byte[] metadata, final CopyListener listener) throws IOException {
        cache.save(key, is, new IoUtils.CopyListener() {
            @Override
            public boolean onBytesCopied(final int current, final int total) {
                return listener == null || listener.onCopied(current);
            }
        });
    }

    @NonNull
    @Override
    public Uri toUri(@NonNull final String key) {
        return CacheProvider.getCacheUri(key);
    }

    @NonNull
    @Override
    public String fromUri(@NonNull final Uri uri) {
        return CacheProvider.getCacheKey(uri);
    }
}
