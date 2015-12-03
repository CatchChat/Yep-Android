package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/10/14.
 */
public class UserLoader extends AsyncTaskLoader<TaskResponse<User>> {
    private final String mId;
    private Account mAccount;

    public UserLoader(final Context context, final Account account, final String id) {
        super(context);
        mAccount = account;
        mId = id;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public TaskResponse<User> loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            return TaskResponse.getInstance(yep.showUser(mId));
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

}
