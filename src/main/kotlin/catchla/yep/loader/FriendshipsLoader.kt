package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.model.Friendship
import catchla.yep.model.FriendshipCursorIndices
import catchla.yep.model.FriendshipTableInfo
import catchla.yep.provider.YepDataStore.Friendships
import catchla.yep.util.Utils
import org.mariotaku.sqliteqb.library.Expression

/**
 * Created by mariotaku on 15/5/27.
 */
class FriendshipsLoader(
        context: Context,
        account: Account
) : ObjectCursorLoader<Friendship>(context, FriendshipCursorIndices::class.java) {

    init {
        uri = Friendships.CONTENT_URI
        projection = FriendshipTableInfo.COLUMNS
        selection = Expression.equalsArgs(Friendships.ACCOUNT_ID).sql
        selectionArgs = arrayOf(Utils.getAccountId(context, account))
    }

}
