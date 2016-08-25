package catchla.yep.activity

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import catchla.yep.Constants
import catchla.yep.Constants.EXTRA_QUERY
import catchla.yep.R
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.set
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.Friendships
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import catchla.yep.view.holder.FriendViewHolder
import catchla.yep.view.holder.SearchSectionHeaderViewHolder
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.mariotaku.sqliteqb.library.Columns.Column
import org.mariotaku.sqliteqb.library.Expression
import java.util.*
import javax.inject.Inject

/**
 * Created by mariotaku on 16/8/25.
 */
class FriendsSearchActivity : ContentActivity(), LoaderManager.LoaderCallbacks<FriendsSearchActivity.FriendSearchResult> {

    val EXTRA_INCLUDE_USER = "include_user"

    private var loaderInitialized: Boolean = false
    private lateinit var adapter: FriendsSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        searchView.queryHint = getString(R.string.search_friends)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query, true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                performSearch(newText, false)
                return true
            }

        })
        adapter = FriendsSearchAdapter(this)

        adapter.friendshipClickListener = { friendship, holder ->
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(Constants.EXTRA_ACCOUNT, account)
            intent.putExtra(Constants.EXTRA_USER, friendship.friend)
            startActivity(intent)
        }
        adapter.userClickListener = { user, holder ->
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(Constants.EXTRA_ACCOUNT, account)
            intent.putExtra(Constants.EXTRA_USER, user)
            startActivity(intent)
        }
        resultsList.adapter = adapter
        resultsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        suggestionsListContainer.visibility = View.GONE
        resultsListContainer.visibility = View.VISIBLE
        resultsListProgress.visibility = View.GONE
        resultsList.visibility = View.VISIBLE
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<FriendsSearchActivity.FriendSearchResult> {
        val query = args.getString(EXTRA_QUERY)
        val includeUser = args.getBoolean(EXTRA_INCLUDE_USER)
        return FriendSearchLoader(this, account, query, includeUser)
    }

    override fun onLoadFinished(loader: Loader<FriendSearchResult>, data: FriendSearchResult) {
        adapter.data = data
    }

    override fun onLoaderReset(loader: Loader<FriendSearchResult>) {
        adapter.data = null
    }

    private fun performSearch(query: String, includeUser: Boolean) {
        if (query.isEmpty()) {
            return
        }
        val args = Bundle {
            this[EXTRA_QUERY] = query
            this[EXTRA_INCLUDE_USER] = includeUser
        }
        if (!loaderInitialized) {
            loaderInitialized = true
            supportLoaderManager.initLoader(0, args, this)
        } else {
            supportLoaderManager.restartLoader(0, args, this)
        }
    }


    data class FriendSearchResult(
            val query: String,
            val friends: List<Friendship>,
            val users: List<User>? = null
    )

    class FriendSearchLoader(
            context: Context,
            val account: Account,
            val query: String, val includeUser: Boolean
    ) : AsyncTaskLoader<FriendSearchResult>(context) {
        override fun loadInBackground(): FriendSearchResult {
            val pattern = "'%'||?||'%'"
            val escape = "^"
            val friendWhere = Expression.or(
                    Expression.likeRaw(Column(Friendships.NAME), pattern, escape),
                    Expression.likeRaw(Column(Friendships.CONTACT_NAME), pattern, escape),
                    Expression.likeRaw(Column(Friendships.REMARKED_NAME), pattern, escape)
            ).sql
            val escapedQuery = query.replace("([_%])".toRegex(), "^$1")
            val friendWhereArgs = Array(3) { escapedQuery }
            val friends = context.contentResolver.query(Friendships.CONTENT_URI, FriendshipTableInfo.COLUMNS,
                    friendWhere, friendWhereArgs, null).use {
                val indices = FriendshipCursorIndices(it)
                val list = ArrayList<Friendship>()
                it.moveToFirst()
                while (!it.isAfterLast) {
                    list.add(indices.newObject(it))
                    it.moveToNext()
                }
                return@use list
            }
            if (!includeUser) {
                return FriendSearchResult(query, friends)
            }
            var users: List<User>? = null
            try {
                val yep = YepAPIFactory.getInstance(context, account)
                val paging = Paging()
                users = yep.searchUsers(query, paging).filter { user ->
                    friends.indexOfLast { friend -> friend.userId == user.id } < 0
                }
            } catch (e: YepException) {

            }
            return FriendSearchResult(query, friends, users)
        }

        override fun onStartLoading() {
            forceLoad()
        }
    }

    class FriendsSearchAdapter(val context: Context) : SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>(), IBaseRecyclerViewAdapter {

        @Inject
        lateinit override var imageLoader: ImageLoaderWrapper

        var data: FriendSearchResult? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        var friendshipClickListener: ((Friendship, FriendViewHolder) -> Unit)? = null
        var userClickListener: ((User, FriendViewHolder) -> Unit)? = null

        private val inflater: LayoutInflater

        init {
            inflater = LayoutInflater.from(context)
            //noinspection unchecked
            GeneralComponentHelper.build(context).inject(this)
        }

        override fun getSectionCount(): Int {
            val data = data ?: return 0
            return if (data.users?.isNotEmpty() ?: false) 2 else 1
        }

        override fun getItemCount(section: Int): Int {
            when (section) {
                0 -> return data?.friends?.size ?: 0
                1 -> return data?.users?.size ?: 0
            }
            throw AssertionError()
        }

        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, section: Int) {
            val h = holder as SearchSectionHeaderViewHolder
            when (section) {
                0 -> {
                    h.display(context.getString(R.string.friends))
                }
                1 -> {
                    h.display(context.getString(R.string.users))
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, section: Int, relativePosition: Int, absolutePosition: Int) {
            when (section) {
                0 -> {
                    val h = holder as FriendViewHolder
                    h.dataPosition = relativePosition
                    h.displayFriendship(data!!.friends[relativePosition], data!!.query)
                }
                1 -> {
                    val h = holder as FriendViewHolder
                    h.dataPosition = relativePosition
                    h.displayUser(data!!.users!![relativePosition], data!!.query)
                }
            }
        }

        override fun getHeaderViewType(section: Int): Int {
            return ITEM_VIEW_TYPE_HEADER
        }

        override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int {
            when (section) {
                0 -> return ITEM_VIEW_TYPE_FRIENDSHIP
                1 -> return ITEM_VIEW_TYPE_USER
            }
            throw AssertionError()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                ITEM_VIEW_TYPE_HEADER -> {
                    val view = inflater.inflate(R.layout.list_item_search_section_header, parent, false)
                    return SearchSectionHeaderViewHolder(view)
                }
                ITEM_VIEW_TYPE_FRIENDSHIP -> {
                    val view = inflater.inflate(R.layout.list_item_friend, parent, false)
                    return FriendViewHolder(view, this, listener = { position, holder ->
                        val h = holder as FriendViewHolder
                        friendshipClickListener?.invoke(data!!.friends[h.dataPosition], h)
                    }, displayUsername = true, displayLastSeen = false, displayBadge = false)
                }
                ITEM_VIEW_TYPE_USER -> {
                    val view = inflater.inflate(R.layout.list_item_friend, parent, false)
                    return FriendViewHolder(view, this, listener = { position, holder ->
                        val h = holder as FriendViewHolder
                        userClickListener?.invoke(data!!.users!![h.dataPosition], h)
                    }, displayUsername = true, displayLastSeen = false, displayBadge = false)
                }
            }
            throw AssertionError()
        }

        companion object {
            val ITEM_VIEW_TYPE_HEADER = 1
            val ITEM_VIEW_TYPE_FRIENDSHIP = 2
            val ITEM_VIEW_TYPE_USER = 3
        }
    }
}