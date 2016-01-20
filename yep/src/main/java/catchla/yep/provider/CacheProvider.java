package catchla.yep.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.j256.simplemagic.ContentInfoUtil;
import com.nostra13.universalimageloader.cache.disc.DiskCache;


import org.mariotaku.mediaviewer.library.FileCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.util.dagger.GeneralComponentHelper;
import okio.ByteString;

/**
 * Created by mariotaku on 16/1/1.
 */
public class CacheProvider extends ContentProvider {
    @Inject
    FileCache mSimpleDiskCache;
    private ContentInfoUtil mContentInfoUtil;

    public static Uri getCacheUri(String key) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(Constants.AUTHORITY_YEP_CACHE)
                .appendPath(ByteString.encodeUtf8(key).base64Url())
                .build();
    }

    public static String getCacheKey(Uri uri) {
        if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()))
            throw new IllegalArgumentException(uri.toString());
        if (!Constants.AUTHORITY_YEP_CACHE.equals(uri.getAuthority()))
            throw new IllegalArgumentException(uri.toString());
        return ByteString.decodeBase64(uri.getLastPathSegment()).utf8();
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        assert context != null;
        mContentInfoUtil = new ContentInfoUtil();
        GeneralComponentHelper.build(context).inject(this);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        try {
            final File file = mSimpleDiskCache.get(getCacheKey(uri));
            if (file == null) return null;
            return mContentInfoUtil.findMatch(file).getMimeType();
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        try {
            final File file = mSimpleDiskCache.get(getCacheKey(uri));
            if (file == null) throw new FileNotFoundException();
            final int modeBits = modeToMode(mode);
            if (modeBits != ParcelFileDescriptor.MODE_READ_ONLY)
                throw new IllegalArgumentException("Cache can't be opened for write");
            return ParcelFileDescriptor.open(file, modeBits);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }

    /**
     * Copied from ContentResolver.java
     */
    private static int modeToMode(String mode) {
        int modeBits;
        if ("r".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_ONLY;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return modeBits;
    }

}
