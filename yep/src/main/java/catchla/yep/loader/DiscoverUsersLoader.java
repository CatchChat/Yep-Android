package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.Paging;
import catchla.yep.model.User;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverUsersLoader extends CachedYepListLoader<User> implements Constants {

    private final DiscoverQuery mQuery;
    private final Paging mPaging;

    public DiscoverUsersLoader(Context context, Account account, DiscoverQuery query, List<User> oldData,
                               Paging paging, boolean readCache, boolean writeCache) {
        super(context, account, User.class, oldData, readCache, writeCache);
        mQuery = query;
        mPaging = paging;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "discover_cache_" + getAccount().name;
    }

    @Override
    protected List<User> requestData(final YepAPI yep, List<User> oldData) throws YepException {
        final List<User> list = new ArrayList<>();
        if (oldData != null) {
            list.addAll(oldData);
        }
        for (User topic : yep.getDiscover(mQuery, mPaging)) {
            list.remove(topic);
            list.add(topic);
        }
        return list;
    }


}
