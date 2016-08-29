package catchla.yep.activity

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.activity.QuickSearchActivity.HideInputOnTouchListener
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.adapter.decorator.DividerItemDecoration
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.loader.TopicsSearchLoader
import catchla.yep.model.Paging
import catchla.yep.model.Topic
import catchla.yep.util.ContentListScrollListener
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.list_item_topic_hot_word_suggestion.view.*
import net.kibotu.android.recyclerviewpresenter.Presenter
import net.kibotu.android.recyclerviewpresenter.PresenterAdapter
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.successUi

class TopicsSearchActivity : ContentActivity(), LoaderManager.LoaderCallbacks<List<Topic>?> {

    lateinit var hotWordsAdapter: PresenterAdapter<Any>
    lateinit var topicsAdapter: TopicsAdapter
    private var loaderInitialized: Boolean = false
    private val contentListSupport: ContentListScrollListener.ContentListSupport = object : ContentListScrollListener.ContentListSupport {
        override val reachingEnd: Boolean
            get() {
                val lm = resultsList.layoutManager as LinearLayoutManager
                return lm.findLastCompletelyVisibleItemPosition() >= lm.itemCount - 1
            }
        override val reachingStart: Boolean
            get() {
                val lm = resultsList.layoutManager as LinearLayoutManager
                return lm.findFirstCompletelyVisibleItemPosition() <= 0
            }

        override fun setControlVisible(visible: Boolean) {

        }

        override fun onLoadMoreContents(position: Int) {
            val topicsCount = topicsAdapter.topicsCount
            if (position == IndicatorPosition.END && !refreshing && topicsCount > 0) {
                topicsAdapter.loadMoreIndicatorPosition = position
                performSearch(searchView.query.toString(), topicsAdapter.getTopic(topicsCount - 1).id)
            }
        }

        override val refreshing: Boolean
            get() = supportLoaderManager.hasRunningLoaders()
        override val contentAdapter: Any?
            get() = topicsAdapter

    }
    private val viewCallback: ContentListScrollListener.ViewCallback = object : ContentListScrollListener.ViewCallback {
        override val isComputingLayout: Boolean
            get() = resultsList.isComputingLayout

        override fun post(runnable: Runnable) {
            resultsList.post(runnable)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        hotWordsAdapter = PresenterAdapter()
        topicsAdapter = TopicsAdapter(this)

        suggestionsList.adapter = hotWordsAdapter
        suggestionsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        suggestionsList.layoutParams = (suggestionsList.layoutParams as FrameLayout.LayoutParams).apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }
        suggestionsList.overScrollMode = View.OVER_SCROLL_NEVER

        resultsList.adapter = topicsAdapter
        resultsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultsList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        resultsList.addOnScrollListener(ContentListScrollListener(contentListSupport, viewCallback))

        searchView.queryHint = getString(R.string.search_topics)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                performSearch(newText)
                return true
            }
        })

        hotWordsAdapter.setOnItemClickListener { item, view, pos ->
            if (item is String) {
                searchView.setQuery(item, true)
            }
        }

        resultsListContainer.touchInterceptor = HideInputOnTouchListener(this, searchView)
        suggestionsListContainer.touchInterceptor = HideInputOnTouchListener(this, searchView)

        suggestionsListContainer.visibility = View.VISIBLE
        resultsListContainer.visibility = View.GONE

        if (!intent.hasExtra(EXTRA_USER_ID)) {
            task {
                val yep = YepAPIFactory.getInstance(this, account)
                return@task yep.getHotWords()
            }.successUi {
                hotWordsAdapter.clear()
                hotWordsAdapter.add(HotWordTitle, HotWordTitlePresenter::class.java)
                for (hotWord in it) {
                    hotWordsAdapter.add(hotWord, HotWordPresenter::class.java)
                }
                hotWordsAdapter.notifyDataSetChanged()
            }
            suggestionsListContainer.visibility = View.VISIBLE
            resultsListContainer.visibility = View.GONE
        } else {
            suggestionsListContainer.visibility = View.GONE
            resultsListContainer.visibility = View.VISIBLE

            resultsListProgress.visibility = View.GONE
            resultsList.visibility = View.VISIBLE
        }

    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<List<Topic>?> {
        val query = args.getString(EXTRA_QUERY)
        val maxId = args.getString(EXTRA_MAX_ID)
        val paging = Paging()
        if (maxId != null) {
            paging.maxId(maxId)
        } else {
            resultsList.visibility = View.GONE
            resultsListProgress.show()
        }
        val oldData: List<Topic>? = topicsAdapter.topics
        val userId = intent.getStringExtra(EXTRA_USER_ID)
        val skillId = intent.getStringExtra(EXTRA_SKILL_ID)
        return TopicsSearchLoader(this, account, query, userId = userId, skillId = skillId,
                paging = paging, oldData = oldData)
    }

    override fun onLoadFinished(loader: Loader<List<Topic>?>, data: List<Topic>?) {
        resultsList.visibility = View.VISIBLE
        resultsListProgress.hide()
        val adapter = topicsAdapter
        adapter.topics = data
        adapter.highlight = searchView.query?.toString()
        adapter.loadMoreSupportedPosition = if (data != null && !data.isEmpty()) {
            IndicatorPosition.END
        } else {
            IndicatorPosition.NONE
        }
        adapter.loadMoreIndicatorPosition = IndicatorPosition.NONE
    }

    override fun onLoaderReset(loader: Loader<List<Topic>?>) {

    }

    private fun performSearch(query: String, maxId: String? = null) {
        if (query.isEmpty()) {
            suggestionsListContainer.visibility = View.VISIBLE
            resultsListContainer.visibility = View.GONE
            return
        }
        suggestionsListContainer.visibility = View.GONE
        resultsListContainer.visibility = View.VISIBLE
        val args = Bundle {
            putString(EXTRA_QUERY, query)
            putString(EXTRA_MAX_ID, maxId)
        }
        if (!loaderInitialized) {
            loaderInitialized = true
            supportLoaderManager.initLoader(0, args, this)
        } else {
            supportLoaderManager.restartLoader(0, args, this)
        }
    }


    class HotWordPresenter(adapter: PresenterAdapter<Any>) : Presenter<Any, HotWordViewHolder>(adapter) {
        override fun getLayout(): Int = R.layout.list_item_topic_hot_word_suggestion
        override fun createViewHolder(layout: Int, parent: ViewGroup): HotWordViewHolder {
            return HotWordViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false), presenterAdapter)
        }

        override fun bindViewHolder(holder: HotWordViewHolder, item: Any, position: Int) {
            val hotWord = item as String
            holder.display(hotWord)
        }
    }

    class HotWordViewHolder(itemView: View, adapter: PresenterAdapter<Any>) : RecyclerView.ViewHolder(itemView) {

        val hotWordView: TextView by lazy { itemView.hotWord }

        init {
            hotWordView.setOnClickListener {
                adapter.onItemClickListener?.onItemClick(adapter.get(layoutPosition), itemView, layoutPosition)
            }
        }

        fun display(hotWord: String) {
            hotWordView.text = hotWord
        }

    }

    object HotWordTitle

    class HotWordTitlePresenter(adapter: PresenterAdapter<Any>) : Presenter<Any, HotWordTitleViewHolder>(adapter) {
        override fun getLayout(): Int = R.layout.list_item_topic_hot_word_suggestion_title
        override fun createViewHolder(layout: Int, parent: ViewGroup): HotWordTitleViewHolder {
            return HotWordTitleViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))
        }

        override fun bindViewHolder(holder: HotWordTitleViewHolder, item: Any, position: Int) {
            holder.display()
        }
    }

    class HotWordTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val hotWordView: TextView by lazy { itemView.hotWord }

        fun display() {
            hotWordView.setText(R.string.hot_word_suggestion_hint)
        }

    }

}
