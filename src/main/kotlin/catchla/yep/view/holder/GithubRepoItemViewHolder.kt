package catchla.yep.view.holder

import android.app.Activity
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.GithubUserAdapter
import catchla.yep.model.GithubRepo
import catchla.yep.util.Utils
import java.util.*

/**
 * Created by mariotaku on 15/6/4.
 */
class GithubRepoItemViewHolder(itemView: View, private val adapter: GithubUserAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val repoNameView: TextView
    private val starCountView: TextView
    private val descriptionView: TextView

    init {
        itemView.setOnClickListener(this)

        repoNameView = itemView.findViewById(R.id.repo_name) as TextView
        starCountView = itemView.findViewById(R.id.stars_count) as TextView
        descriptionView = itemView.findViewById(R.id.description) as TextView
    }

    override fun onClick(v: View) {
        val repo = adapter.getRepoAt(layoutPosition) ?: return
        Utils.openUri(adapter.context as Activity, Uri.parse(repo.htmlUrl))
    }

    fun displayRepo(repo: GithubRepo) {
        repoNameView.text = repo.name
        starCountView.text = String.format(Locale.getDefault(), "%d \u2605", repo.stargazersCount)
        descriptionView.text = repo.description
        descriptionView.visibility = if (TextUtils.isEmpty(repo.description)) View.GONE else View.VISIBLE
    }
}
