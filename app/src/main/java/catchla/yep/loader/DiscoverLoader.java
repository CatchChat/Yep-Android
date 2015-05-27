package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverLoader extends AsyncTaskLoader<TaskResponse<PagedUsers>> {

    private final Account mAccount;
    private final DiscoverQuery mQuery;

    public DiscoverLoader(Context context, Account account, DiscoverQuery query) {
        super(context);
        mAccount = account;
        mQuery = query;
    }

    @Override
    public TaskResponse<PagedUsers> loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            return TaskResponse.getInstance(yep.getDiscover(mQuery));
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
