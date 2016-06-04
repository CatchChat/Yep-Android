package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import catchla.yep.R
import catchla.yep.model.GithubRepo
import catchla.yep.model.GithubUserInfo
import catchla.yep.view.holder.GithubRepoItemViewHolder
import catchla.yep.view.holder.GithubUserHeaderViewHolder

/**
 * Created by mariotaku on 15/6/4.
 */
class GithubUserAdapter(context: Context) : LoadMoreSupportAdapter<RecyclerView.ViewHolder>(context) {

    private val mInflater: LayoutInflater

    var info: GithubUserInfo? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val view = mInflater.inflate(R.layout.header_github_user, parent, false)
            return GithubUserHeaderViewHolder(view, this)
        }
        val view = mInflater.inflate(R.layout.list_item_github_repo, parent, false)
        return GithubRepoItemViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as GithubUserHeaderViewHolder).displayUser(info)
        } else {
            (holder as GithubRepoItemViewHolder).displayRepo(info!!.repos[position - 1])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_HEADER
        return VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        if (info == null) return 0
        return info!!.repos.size + 1
    }

    fun getRepoAt(layoutPosition: Int): GithubRepo? {
        if (info == null) return null
        return info!!.repos[layoutPosition - 1]
    }

    companion object {

        private val VIEW_TYPE_HEADER = 1
        private val VIEW_TYPE_ITEM = 2
    }
}
