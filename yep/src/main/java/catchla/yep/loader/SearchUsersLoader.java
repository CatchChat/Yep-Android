package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.Paging;
import catchla.yep.model.User;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class SearchUsersLoader extends CachedYepListLoader<User> implements Constants {

    private final String mQuery;

    public SearchUsersLoader(Context context, Account account, String query) {
        super(context, account, User.class, null, false, false);
        mQuery = query;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "";
    }

    @Override
    protected List<User> requestData(final YepAPI yep, List<User> oldData) throws YepException {
        final Paging paging = new Paging();
        int page = 1;
        final List<User> list = new ArrayList<>();
        PagedUsers users;
        while ((users = yep.searchUsers(mQuery, paging)).size() > 0) {
            list.addAll(users);
            paging.page(++page);
            if (users.getCount() < users.getPerPage()) break;
        }
        return list;
    }


}
