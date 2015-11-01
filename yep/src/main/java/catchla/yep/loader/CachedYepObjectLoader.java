package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepObjectLoader<T> extends CachedYepLoader<T> {

    private final Class<? extends T> mObjectClass;

    public CachedYepObjectLoader(Context context, Account account, Class<? extends T> objectClass, boolean readCache, boolean writeCache) {
        super(context, account, null, readCache, writeCache);
        mObjectClass = objectClass;
    }

    @Override
    protected void serialize(final T data, final FileOutputStream os) throws IOException {
        LoganSquare.serialize(data, os);
    }

    @Override
    protected T deserialize(final FileInputStream is) throws IOException {
        return LoganSquare.parse(is, mObjectClass);
    }
}
