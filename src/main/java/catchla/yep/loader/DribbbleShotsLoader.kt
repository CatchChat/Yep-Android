package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.model.DribbbleShots
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI

/**
 * Created by mariotaku on 15/6/3.
 */
class DribbbleShotsLoader(
        context: Context,
        account: Account,
        private val yepUserId: String,
        readCache: Boolean,
        writeCache: Boolean
) : CachedYepObjectLoader<DribbbleShots>(context, account, DribbbleShots::class.java, readCache, writeCache) {

    override val cacheFileName: String
        get() = "cached_dribbble_shots_${account.name}"

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: DribbbleShots?): DribbbleShots {
        return yep.getDribbbleShots(yepUserId)
    }

}
