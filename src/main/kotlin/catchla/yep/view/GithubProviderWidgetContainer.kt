package catchla.yep.view

import android.accounts.Account
import android.content.Context
import android.util.AttributeSet
import android.view.View
import catchla.yep.model.GithubUserInfo
import catchla.yep.model.TaskResponse
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.provider_widget_github.view.*

/**
 * Created by mariotaku on 16/8/3.
 */
class GithubProviderWidgetContainer : ProviderWidgetContainer<GithubUserInfo> {
    lateinit var account: Account

    lateinit var user: User

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun displayData(result: TaskResponse<GithubUserInfo>) {
        widgetContent.visibility = View.VISIBLE
        loadProgress.visibility = View.GONE
        if (result.data != null) {
            repoCount.text = result.data.user.publicRepos.toString()
            starCount.text = result.data.repos.map { it.stargazersCount }.sum().toString()
        }
    }

    override fun preRequest() {
        widgetContent.visibility = View.GONE
        loadProgress.visibility = View.VISIBLE
    }

    @Throws(YepException::class)
    override fun doRequest(): GithubUserInfo {
        val yep = YepAPIFactory.getInstance(context, account)
        return yep.getGithubUserInfo(user.id)
    }
}
