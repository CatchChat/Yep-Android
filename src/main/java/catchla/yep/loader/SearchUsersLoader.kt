package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.Paging
import catchla.yep.model.ResponseList
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class SearchUsersLoader(
        context: Context,
        account: Account,
        private val query: String
) : CachedYepListLoader<User>(context, account, User::class.java, null, false, false), Constants {
    override val cacheFileName: String
        get() = ""


    @Throws(YepException::class)
    override fun requestData(yep: YepAPI, oldData: List<User>?): List<User> {
        val paging = Paging()
        var page = 1
        val list = ArrayList<User>()
        var users: ResponseList<User>
        do {
            users = yep.searchUsers(query, paging)
            if (users.isEmpty()) break
            list.addAll(users)
            paging.page(++page)
        } while (users.count >= users.perPage)
        return list
    }


}
