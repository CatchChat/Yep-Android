package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Friendship;
import catchla.yep.model.Paging;
import catchla.yep.provider.YepDataStore.Friendships;

/**
 * Created by mariotaku on 15/5/27.
 */
public class FriendshipsLoader extends ObjectCursorLoader<Friendship> {

    private final Account mAccount;
    private final Paging mPaging;

    public FriendshipsLoader(Context context, Account account, Paging paging) {
        super(context, Friendship.Indices.class, Friendships.CONTENT_URI, Friendships.COLUMNS, null,
                null, null);
        mAccount = account;
        mPaging = paging;
    }

}
