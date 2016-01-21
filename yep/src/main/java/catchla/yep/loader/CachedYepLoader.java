package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.mariotaku.mediaviewer.library.FileCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.model.YepException;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.dagger.GeneralComponentHelper;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepLoader<T> extends AsyncTaskLoader<T> implements Constants {

    @Inject
    FileCache mFileCache;
    private final Account mAccount;
    private final T mOldData;
    private final boolean mReadCache, mWriteCache;
    private YepException mException;

    public CachedYepLoader(Context context, Account account, T oldData, boolean readCache, boolean writeCache) {
        super(context);
        //noinspection unchecked
        GeneralComponentHelper.build(context).inject((CachedYepLoader<Object>) this);
        mAccount = account;
        mOldData = oldData;
        mReadCache = readCache;
        mWriteCache = writeCache;
    }

    public YepException getException() {
        return mException;
    }

    @Override
    public T loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            if (mReadCache) {
                FileInputStream is = null;
                try {
                    File cacheFile = mFileCache.get(getCacheFileName());
                    if (cacheFile != null) {
                        is = new FileInputStream(cacheFile);
                        T cached = deserialize(is);
                        if (cached != null) return cached;
                    }
                } catch (IOException e) {
                    // Ignore
                } finally {
                    Utils.closeSilently(is);
                }
            }
            final T data = requestData(yep, mOldData);
            if (mWriteCache) {
                ByteArrayOutputStream os = null;
                try {
                    os = new ByteArrayOutputStream();
                    serialize(data, os);
                    mFileCache.save(getCacheFileName(), new ByteArrayInputStream(os.toByteArray()), null);
                } catch (IOException e) {
                    // Ignore
                } finally {
                    Utils.closeSilently(os);
                }
            }
            return data;
        } catch (YepException e) {
            if (BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
            }
            mException = e;
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    protected abstract void serialize(final T data, final OutputStream os) throws IOException;

    protected abstract T deserialize(final InputStream is) throws IOException;

    @NonNull
    protected abstract String getCacheFileName();

    protected abstract T requestData(final YepAPI yep, final T oldData) throws YepException;

    protected final Account getAccount() {
        return mAccount;
    }

}
