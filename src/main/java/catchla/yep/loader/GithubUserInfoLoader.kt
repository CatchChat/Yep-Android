package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.model.GithubUserInfo
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI

/**
 * Created by mariotaku on 15/6/3.
 */
class GithubUserInfoLoader(
        context: Context,
        account: Account,
        private val yepUserId: String,
        readCache: Boolean,
        writeCache: Boolean
) : CachedYepObjectLoader<GithubUserInfo>(context, account, GithubUserInfo::class.java, readCache, writeCache) {

    override val cacheFileName: String
        get() = "cached_github_user_info_${account.name}"

    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: GithubUserInfo?): GithubUserInfo {
        return yep.getGithubUserInfo(yepUserId)
    }

}
