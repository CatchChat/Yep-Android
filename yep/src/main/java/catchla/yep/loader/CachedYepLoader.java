package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepLoader<T> extends AsyncTaskLoader<TaskResponse<T>> {

    private final Account mAccount;
    private final T mOldData;
    private final boolean mReadCache, mWriteCache;

    public CachedYepLoader(Context context, Account account, T oldData, boolean readCache, boolean writeCache) {
        super(context);
        mAccount = account;
        mOldData = oldData;
        mReadCache = readCache;
        mWriteCache = writeCache;
    }

    @Override
    public TaskResponse<T> loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            final File cacheFile = new File(getContext().getCacheDir(), getCacheFileName());
            if (mReadCache) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(cacheFile);
                    T cached = deserialize(is);
                    if (cached != null) return TaskResponse.getInstance(cached);
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
            return TaskResponse.getInstance(data);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
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
