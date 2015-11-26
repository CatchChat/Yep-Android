package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import org.mariotaku.sqliteqb.library.Expression;

import catchla.yep.model.Friendship;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/5/27.
 */
public class FriendshipsLoader extends ObjectCursorLoader<Friendship> {

    public FriendshipsLoader(Context context, Account account) {
        super(context, Friendship.Indices.class);
        setUri(Friendships.CONTENT_URI);
        setProjection(Friendships.COLUMNS);
        setSelection(Expression.equalsArgs(Friendships.ACCOUNT_ID).getSQL());
        setSelectionArgs(new String[]{Utils.getAccountId(context, account)});
    }

}
