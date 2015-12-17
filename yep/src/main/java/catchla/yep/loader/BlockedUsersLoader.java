package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.Paging;
import catchla.yep.model.ResponseList;
import catchla.yep.model.User;
import catchla.yep.util.YepAPI;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class BlockedUsersLoader extends CachedYepListLoader<User> implements Constants {

    public BlockedUsersLoader(Context context, Account account) {
        super(context, account, User.class, null, false, false);
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "";
    }

    @Override
    protected List<User> requestData(final YepAPI yep, List<User> data) throws YepException {
        final Paging paging = new Paging();
        int page = 1;
        final List<User> list = new ArrayList<>();
        ResponseList<User> users;
        while ((users = yep.getBlockedUsers(paging)).size() > 0) {
            list.addAll(users);
            paging.page(++page);
            if (users.getCount() < users.getPerPage()) break;
        }
        return list;
    }


}
