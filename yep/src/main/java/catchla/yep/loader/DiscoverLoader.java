package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.Paging;
import catchla.yep.model.User;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverLoader extends CachedYepListLoader<User> implements Constants {

    private final DiscoverQuery mQuery;

    public DiscoverLoader(Context context, Account account, DiscoverQuery query, boolean readCache, boolean writeCache) {
        super(context, account, User.class, readCache, writeCache);
        mQuery = query;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "discover_cache_" + getAccount().name;
    }

    @Override
    protected List<User> requestData(final YepAPI yep) throws YepException {
        final Paging paging = new Paging();
        int page = 1;
        final List<User> list = new ArrayList<>();
        PagedUsers users;
        while ((users = yep.getDiscover(mQuery, paging)).size() > 0) {
            list.addAll(users);
            paging.page(++page);
            if (users.getCount() < users.getPerPage()) break;
        }
        return list;
    }


}