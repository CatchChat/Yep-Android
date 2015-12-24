package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.model.YepException;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepLoader<T> extends AsyncTaskLoader<T> implements Constants {

    private final Account mAccount;
    private final T mOldData;
    private final boolean mReadCache, mWriteCache;
    private YepException mException;

    public CachedYepLoader(Context context, Account account, T oldData, boolean readCache, boolean writeCache) {
        super(context);
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
            final File cacheFile = new File(getContext().getCacheDir(), getCacheFileName());
            if (mReadCache) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(cacheFile);
                    T cached = deserialize(is);
                    if (cached != null) return cached;
                } catch (IOException e) {
                    // Ignore
                } finally {
                    Utils.closeSilently(is);
                }
            }
            final T data = requestData(yep, mOldData);
            if (mWriteCache) {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(cacheFile);
                    serialize(data, os);
                    os.flush();
                    os.close();
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

    protected abstract void serialize(final T data, final FileOutputStream os) throws IOException;

    protected abstract T deserialize(final FileInputStream is) throws IOException;

    @NonNull
    protected abstract String getCacheFileName();

    protected abstract T requestData(final YepAPI yep, final T oldData) throws YepException;

    protected final Account getAccount() {
        return mAccount;
    }

}
