package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by mariotaku on 15/6/3.
 */
public abstract class CachedYepListLoader<E> extends CachedYepLoader<List<E>> {

    private final Class<E> mObjectClass;

    public CachedYepListLoader(Context context, Account account, Class<E> objectClass, List<E> oldData,
                               boolean readCache, boolean writeCache) {
        super(context, account, oldData, readCache, writeCache);
        mObjectClass = objectClass;
    }

    @Override
    protected void serialize(final List<E> data, final OutputStream os) throws IOException {
        LoganSquare.serialize(data, os, mObjectClass);
    }

    @Override
    protected List<E> deserialize(final InputStream st) throws IOException {
        return LoganSquare.parseList(st, mObjectClass);
    }
}
