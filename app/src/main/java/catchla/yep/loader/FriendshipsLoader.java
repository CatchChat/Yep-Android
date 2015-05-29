package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.Friendship;
import catchla.yep.model.PagedFriendships;
import catchla.yep.model.Paging;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class FriendshipsLoader extends AsyncTaskLoader<TaskResponse<List<Friendship>>> {

    private final Account mAccount;
    private final Paging mPaging;

    public FriendshipsLoader(Context context, Account account, Paging paging) {
        super(context);
        mAccount = account;
        mPaging = paging;
    }

    @Override
    public TaskResponse<List<Friendship>> loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            final PagedFriendships friendships = yep.getFriendships(mPaging);
            final List<Friendship> list = new ArrayList<>();
            list.addAll(friendships);
            return TaskResponse.getInstance(list);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
