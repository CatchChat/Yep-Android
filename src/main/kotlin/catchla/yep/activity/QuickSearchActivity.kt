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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.adapter.iface.IBaseRecyclerViewAdapter
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.set
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.*
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import catchla.yep.view.holder.SearchSectionHeaderViewHolder
import catchla.yep.view.holder.SimpleMessageViewHolder
import catchla.yep.view.holder.SimpleTopicViewHolder
import catchla.yep.view.holder.SimpleUserViewHolder
import catchla.yep.view.iface.IExtendedView
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.mariotaku.sqliteqb.library.Columns.Column
import org.mariotaku.sqliteqb.library.Expression
import java.util.*
import javax.inject.Inject

/**
 * Created by mariotaku on 16/8/25.
 */
class QuickSearchActivity : ContentActivity(), LoaderManager.LoaderCallbacks<QuickSearchActivity.FriendSearchResult> {

    private var loaderInitialized: Boolean = false
    private lateinit var adapter: FriendsSearchAdapter

    private val includeUser: Boolean
        get() = intent.getBooleanExtra(EXTRA_INCLUDE_USER, true)

    private val includeChatHistory: Boolean
        get() = intent.getBooleanExtra(EXTRA_INCLUDE_CHAT_HISTORY, true)

    private val includeTopics: Boolean
        get() = intent.getBooleanExtra(EXTRA_INCLUDE_TOPICS, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        searchView.queryHint = getString(R.string.search_friends)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            val laterSearchRunnable: Runnable = Runnable {
                performSearch(searchView.query.toString(), true)
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // Cancel search if submit called
                searchView.removeCallbacks(laterSearchRunnable)
                performSearch(query, true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchView.removeCallbacks(laterSearchRunnable)
                performSearch(newText, false)
                // Perform network operation enabled search after 0.5 second
                searchView.postDelayed(laterSearchRunnable, 500L)
                return true
            }
        })
        adapter = FriendsSearchAdapter(this)

        adapter.friendshipClickListener = { friendship, holder ->
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT, account)
            intent.putExtra(EXTRA_USER, friendship.friend)
            startActivity(intent)
        }
        adapter.userClickListener = { user, holder ->
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT, account)
            intent.putExtra(EXTRA_USER, user)
            startActivity(intent)
        }
        adapter.conversationClickListener = { conversation, holder ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT, account)
            intent.putExtra(EXTRA_CONVERSATION, conversation)
            startActivity(intent)
        }
        adapter.messageClickListener = { message, holder ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT, account)
            intent.putExtra(EXTRA_CONVERSATION, Conversation.fromMessage(message, message.accountId))
            startActivity(intent)
        }

        resultsList.adapter = adapter
        resultsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        resultsListContainer.touchInterceptor = HideInputOnTouchListener(this, searchView)
        suggestionsListContainer.touchInterceptor = HideInputOnTouchListener(this, searchView)

        suggestionsListContainer.visibility = View.GONE
        resultsListContainer.visibility = View.VISIBLE
        resultsListProgress.visibility = View.GONE
        resultsList.visibility = View.VISIBLE
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<QuickSearchActivity.FriendSearchResult> {
        val query = args.getString(EXTRA_QUERY)
        val includeUser = args.getBoolean(EXTRA_INCLUDE_USER)
        val includeChatHistory = args.getBoolean(EXTRA_INCLUDE_CHAT_HISTORY)
        val includeTopics = args.getBoolean(EXTRA_INCLUDE_TOPICS)
        return FriendSearchLoader(this, account, query, includeUser, includeChatHistory, includeTopics)
    }

    override fun onLoadFinished(loader: Loader<FriendSearchResult>, data: FriendSearchResult) {
        adapter.data = data
    }

    override fun onLoaderReset(loader: Loader<FriendSearchResult>) {
        adapter.data = null
    }


    private fun performSearch(query: String, performNetworkRequest: Boolean) {
        if (query.isEmpty()) {
            return
        }
        val args = Bundle {
            this[EXTRA_QUERY] = query
            this[EXTRA_INCLUDE_USER] = performNetworkRequest && includeUser
            this[EXTRA_INCLUDE_CHAT_HISTORY] = includeChatHistory
            this[EXTRA_INCLUDE_TOPICS] = includeTopics
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
            val messages: List<Message>? = null,
            val topicConversations: List<Conversation>? = null,
            val users: List<User>? = null
    ) {
        var sections: Int = 0
            private set
        var friendSection: Int = -1
            private set
        var messageSection: Int = -1
            private set
        var topicsSection: Int = -1
            private set
        var usersSection: Int = -1
            private set

        init {
            if (friends.isNotEmpty()) {
                friendSection = sections
                sections += 1
            }
            if (messages?.isNotEmpty() ?: false) {
                messageSection = sections
                sections += 1
            }
            if (topicConversations?.isNotEmpty() ?: false) {
                topicsSection = sections
                sections += 1
            }
            if (users?.isNotEmpty() ?: false) {
                usersSection = sections
                sections += 1
            }
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

        var friendshipClickListener: ((Friendship, SimpleUserViewHolder) -> Unit)? = null
        var userClickListener: ((User, SimpleUserViewHolder) -> Unit)? = null
        var conversationClickListener: ((Conversation, SimpleTopicViewHolder) -> Unit)? = null
        var messageClickListener: ((Message, SimpleMessageViewHolder) -> Unit)? = null

        private val inflater: LayoutInflater

        init {
            inflater = LayoutInflater.from(context)
            //noinspection unchecked
            GeneralComponentHelper.build(context).inject(this)
        }

        override fun getSectionCount(): Int {
            return data?.sections ?: 0
        }

        override fun getItemCount(section: Int): Int {
            val data = data ?: return 0
            when (section) {
                data.friendSection -> return data.friends.size
                data.messageSection -> return data.messages?.size ?: 0
                data.topicsSection -> return data.topicConversations?.size ?: 0
                data.usersSection -> return data.users?.size ?: 0
            }
            throw AssertionError()
        }

        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, section: Int) {
            val data = data ?: return
            val h = holder as SearchSectionHeaderViewHolder
            when (section) {
                data.friendSection -> {
                    h.display(context.getString(R.string.friends))
                }
                data.messageSection -> {
                    h.display(context.getString(R.string.chat_history))
                }
                data.topicsSection -> {
                    h.display(context.getString(R.string.joined_topics))
                }
                data.usersSection -> {
                    h.display(context.getString(R.string.users))
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, section: Int, relativePosition: Int, absolutePosition: Int) {
            val data = data ?: return
            when (section) {
                data.friendSection -> {
                    val h = holder as SimpleUserViewHolder
                    h.dataPosition = relativePosition
                    h.display(data.friends[relativePosition].friend, data.query)
                }
                data.usersSection -> {
                    val h = holder as SimpleUserViewHolder
                    h.dataPosition = relativePosition
                    h.display(data.users!![relativePosition], data.query)
                }
                data.messageSection -> {
                    val h = holder as SimpleMessageViewHolder
                    h.dataPosition = relativePosition
                    h.display(data.messages!![relativePosition], data.query)
                }
                data.topicsSection -> {
                    val h = holder as SimpleTopicViewHolder
                    h.dataPosition = relativePosition
                    h.display(data.topicConversations!![relativePosition].circle.topic, data.query)
                }
            }
        }

        override fun getHeaderViewType(section: Int): Int {
            return ITEM_VIEW_TYPE_HEADER
        }

        override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int {
            val data = data ?: return -1
            when (section) {
                data.friendSection -> return ITEM_VIEW_TYPE_FRIENDSHIP
                data.messageSection -> return ITEM_VIEW_TYPE_MESSAGE
                data.topicsSection -> return ITEM_VIEW_TYPE_TOPIC
                data.usersSection -> return ITEM_VIEW_TYPE_USER
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
                    val view = inflater.inflate(R.layout.list_item_user_simple, parent, false)
                    return SimpleUserViewHolder(view, this, displayLastSeen = true) { position, holder ->
                        friendshipClickListener?.invoke(data!!.friends[holder.dataPosition], holder)
                    }
                }
                ITEM_VIEW_TYPE_USER -> {
                    val view = inflater.inflate(R.layout.list_item_user_simple, parent, false)
                    return SimpleUserViewHolder(view, this, displayLastSeen = false) { position, holder ->
                        userClickListener?.invoke(data!!.users!![holder.dataPosition], holder)
                    }
                }
                ITEM_VIEW_TYPE_MESSAGE -> {
                    val view = inflater.inflate(R.layout.list_item_message_simple, parent, false)
                    return SimpleMessageViewHolder(view, this) { position, holder ->
                        messageClickListener?.invoke(data!!.messages!![holder.dataPosition], holder)
                    }
                }
                ITEM_VIEW_TYPE_TOPIC -> {
                    val view = inflater.inflate(R.layout.list_item_topic_simple, parent, false)
                    return SimpleTopicViewHolder(view, this) { position, holder ->
                        conversationClickListener?.invoke(data!!.topicConversations!![holder.dataPosition], holder)
                    }
                }
            }
            throw AssertionError()
        }

        companion object {
            val ITEM_VIEW_TYPE_HEADER = 1
            val ITEM_VIEW_TYPE_FRIENDSHIP = 2
            val ITEM_VIEW_TYPE_USER = 3
            val ITEM_VIEW_TYPE_MESSAGE = 4
            val ITEM_VIEW_TYPE_TOPIC = 5

        }
    }

    class FriendSearchLoader(
            context: Context,
            val account: Account,
            val query: String,
            val includeUser: Boolean,
            val includeChatHistory: Boolean,
            val includeTopics: Boolean
    ) : AsyncTaskLoader<FriendSearchResult>(context) {
        override fun loadInBackground(): FriendSearchResult {
            val accountId = Utils.getAccountId(context, account)
            val pattern = "'%'||?||'%'"
            val escape = "^"
            val friendWhere = Expression.and(
                    Expression.equalsArgs(Friendships.ACCOUNT_ID),
                    Expression.or(
                            Expression.likeRaw(Column(Friendships.NAME), pattern, escape),
                            Expression.likeRaw(Column(Friendships.CONTACT_NAME), pattern, escape),
                            Expression.likeRaw(Column(Friendships.REMARKED_NAME), pattern, escape)
                    )
            ).sql
            val escapedQuery = query.replace("([_%])".toRegex(), "^$1")
            val friendWhereArgs = arrayOf(accountId, escapedQuery, escapedQuery, escapedQuery)
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
            var messages: List<Message>? = null
            var users: List<User>? = null
            var topicConversations: List<Conversation>? = null
            if (includeChatHistory) {
                val messageWhere = Expression.and(
                        Expression.equalsArgs(Messages.ACCOUNT_ID),
                        Expression.likeRaw(Column(Messages.TEXT_CONTENT), pattern, escape)
                ).sql
                val messageWhereArgs = arrayOf(accountId, escapedQuery)
                messages = context.contentResolver.query(Messages.CONTENT_URI, MessageTableInfo.COLUMNS,
                        messageWhere, messageWhereArgs, null).use {
                    val indices = MessageCursorIndices(it)
                    val list = ArrayList<Message>()
                    it.moveToFirst()
                    while (!it.isAfterLast) {
                        list.add(indices.newObject(it))
                        it.moveToNext()
                    }
                    return@use list
                }
            }
            if (includeTopics) {
                val messageWhere = Expression.and(
                        Expression.equalsArgs(Conversations.ACCOUNT_ID),
                        Expression.equalsArgs(Conversations.RECIPIENT_TYPE),
                        Expression.likeRaw(Column(Conversations.TITLE), pattern, escape)
                ).sql
                val messageWhereArgs = arrayOf(accountId, Message.RecipientType.CIRCLE, escapedQuery)
                topicConversations = context.contentResolver.query(Conversations.CONTENT_URI, ConversationTableInfo.COLUMNS,
                        messageWhere, messageWhereArgs, null).use {
                    val indices = ConversationCursorIndices(it)
                    val list = ArrayList<Conversation>()
                    it.moveToFirst()
                    while (!it.isAfterLast) {
                        val conversation = indices.newObject(it)
                        if (conversation?.circle?.topic != null) {
                            list.add(conversation)
                        }
                        it.moveToNext()
                    }
                    return@use list
                }
            }
            if (includeUser) {
                try {
                    val yep = YepAPIFactory.getInstance(context, account)
                    val paging = Paging()
                    users = yep.searchUsers(query, paging).filter { user ->
                        friends.indexOfLast { friend -> friend.userId == user.id } < 0
                    }
                } catch (e: YepException) {

                }
            }
            if (includeTopics) {

            }
            return FriendSearchResult(query, friends, messages, topicConversations, users)
        }

        override fun onStartLoading() {
            forceLoad()
        }
    }

    class HideInputOnTouchListener(val context: Context, val targetView: View) : IExtendedView.TouchInterceptor {
        override fun dispatchTouchEvent(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(targetView.windowToken, 0)
            }
            return super.dispatchTouchEvent(view, event)
        }
    }

}