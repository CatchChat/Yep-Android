package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.model.InstagramMediaList
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI

/**
 * Created by mariotaku on 15/6/3.
 */
class InstagramMediaLoader(
        context: Context,
        account: Account,
        private val yepUserId: String,
        readCache: Boolean,
        writeCache: Boolean
) : CachedYepObjectLoader<InstagramMediaList>(context, account, InstagramMediaList::class.java, readCache, writeCache) {

    override val cacheFileName: String
        get() = "cached_instagram_media_" + account.name

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: InstagramMediaList): InstagramMediaList {
        return yep.getInstagramMediaList(yepUserId)
    }

}
