package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import catchla.yep.model.TaskResponse
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory

/**
 * Created by mariotaku on 15/10/14.
 */
class UserLoader(context: Context, private val account: Account, private val id: String) : AsyncTaskLoader<TaskResponse<User>>(context) {

    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): TaskResponse<User> {
        val yep = YepAPIFactory.getInstance(context, account)
        try {
            return TaskResponse.getInstance(yep.showUser(id))
        } catch (e: YepException) {
            return TaskResponse.getInstance<User>(e)
        }

    }

}
