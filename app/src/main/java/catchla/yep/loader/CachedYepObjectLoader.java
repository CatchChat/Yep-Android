package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepObjectLoader<T> extends AsyncTaskLoader<TaskResponse<T>> {

    private final Account mAccount;
    private final Class<? extends T> mObjectClass;
    private final boolean mReadCache, mWriteCache;

    public CachedYepObjectLoader(Context context, Account account, Class<? extends T> objectClass, boolean readCache, boolean writeCache) {
        super(context);
        mAccount = account;
        mObjectClass = objectClass;
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
                    T cached = LoganSquare.parse(is, mObjectClass);
                    if (cached != null) return TaskResponse.getInstance(cached);
                } catch (IOException e) {
                    // Ignore
                } finally {
                    Utils.closeSilently(is);
                }
            }
            final T data = requestData(yep);
            if (mWriteCache) {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(cacheFile);
                    LoganSquare.serialize(data, os);
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

    @NonNull
    protected abstract String getCacheFileName();

    protected abstract T requestData(final YepAPI yep) throws YepException;

    protected final Account getAccount() {
        return mAccount;
    }

}
