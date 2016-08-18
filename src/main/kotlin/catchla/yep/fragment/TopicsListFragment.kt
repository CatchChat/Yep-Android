package catchla.yep.fragment

import android.accounts.Account
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.activity.*
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition
import catchla.yep.extension.account
import catchla.yep.extension.Bundle
import catchla.yep.fragment.iface.IActionButtonSupportFragment
import catchla.yep.loader.DiscoverTopicsLoader
import catchla.yep.model.*
import catchla.yep.util.YepAPIFactory
import catchla.yep.view.holder.SkillTopicRelatedUsersViewHolder
import catchla.yep.view.holder.TopicViewHolder

/**
 * Created by mariotaku on 15/10/12.
 */
class TopicsListFragment : AbsContentListRecyclerViewFragment<TopicsAdapter>(),
        LoaderManager.LoaderCallbacks<List<Topic>?>, TopicsAdapter.TopicClickListener,
        IActionButtonSupportFragment {
    val relatedUsersLoaderCallback = object : LoaderManager.LoaderCallbacks<List<User>?> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<User>?> {
            return RelatedUsersLoader(context, account, skill!!)
        }

        override fun onLoaderReset(loader: Loader<List<User>?>) {

        }

        override fun onLoadFinished(loader: Loader<List<User>?>, data: List<User>?) {
            adapter.relatedUsers = data
        }

    }

    @TopicSortOrder
    private var mSortBy: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //noinspection WrongConstant
        mSortBy = preferences.getString(KEY_TOPICS_SORT_ORDER, TopicSortOrder.DEFAULT)
        setHasOptionsMenu(true)
        val fragmentArgs = arguments
        val loaderArgs = Bundle()
        if (fragmentArgs != null) {
            loaderArgs.putBoolean(EXTRA_READ_CACHE, !fragmentArgs.containsKey(EXTRA_LEARNING) && !fragmentArgs.containsKey(EXTRA_MASTER))
            if (fragmentArgs.containsKey(EXTRA_USER_ID)) {
                loaderArgs.putString(EXTRA_USER_ID, fragmentArgs.getString(EXTRA_USER_ID))
            }
        } else {
            loaderArgs.putBoolean(EXTRA_READ_CACHE, true)
        }
        loaderManager.initLoader(0, loaderArgs, this)
        skill?.let {
            loaderManager.initLoader(1, null, relatedUsersLoaderCallback)
        }
        adapter.clickListener = this
        adapter.showSkillLabel = skill == null
        showProgress()
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<List<Topic>?> {
        val cachingEnabled = isCachingEnabled
        val readCache = args.getBoolean(EXTRA_READ_CACHE) && cachingEnabled
        val readOld = args.getBoolean(EXTRA_READ_OLD, readCache)
        val maxId = args.getString(EXTRA_MAX_ID)
        val paging = Paging()
        if (maxId != null) {
            paging.maxId(maxId)
        }
        val oldData: List<Topic>?
        if (readOld) {
            oldData = adapter.topics
        } else {
            oldData = null
        }
        return DiscoverTopicsLoader(activity, account, arguments.getString(EXTRA_USER_ID),
                skill?.id, paging, sortOrder, readCache, cachingEnabled, oldData)
    }

    private val sortOrder: String
        @TopicSortOrder
        get() {
            if (hasUserId()) return TopicSortOrder.TIME
            return if (mSortBy != null) mSortBy!! else TopicSortOrder.TIME
        }

    private val skill: Skill?
        get() = arguments.getParcelable(EXTRA_SKILL)

    private fun hasUserId(): Boolean {
        val fragmentArgs = arguments
        return fragmentArgs != null && fragmentArgs.containsKey(EXTRA_USER_ID)
    }

    override fun onLoadFinished(loader: Loader<List<Topic>?>, data: List<Topic>?) {
        val adapter = adapter
        adapter.topics = data
        adapter.loadMoreSupportedPosition = if (data != null && !data.isEmpty()) IndicatorPosition.END else IndicatorPosition.NONE
        showContent()
        refreshing = false
        refreshEnabled = true
        loadMoreIndicatorPosition = IndicatorPosition.NONE
    }

    override fun onLoaderReset(loader: Loader<List<Topic>?>) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_NEW_LOCATION_TOPIC -> {
                if (resultCode == Activity.RESULT_OK) {
                    val name = data!!.getStringExtra(EXTRA_NAME)
                    val location = data.getParcelableExtra<Location>(EXTRA_LOCATION)
                    val intent = Intent(context, NewTopicActivity::class.java)
                    val attachment = LocationAttachment()
                    attachment.place = name
                    attachment.latitude = location.latitude
                    attachment.longitude = location.longitude
                    intent.putExtra(EXTRA_NEW_TOPIC_TYPE, NewTopicActivity.TYPE_LOCATION)
                    intent.putExtra(EXTRA_ATTACHMENT, attachment)
                    intent.putExtra(EXTRA_ACCOUNT, account)
                    startActivity(intent)
                    return
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateAdapter(context: Context): TopicsAdapter {
        return TopicsAdapter(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_fragment_chats_list, menu)
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
    }

    override fun onRefresh() {
        val loaderArgs = Bundle {
            putBoolean(EXTRA_READ_CACHE, false)
        }
        loaderManager.restartLoader(0, loaderArgs, this)
    }

    override var refreshing: Boolean
        get() = loaderManager.hasRunningLoaders()
        set(value) {
            super.refreshing = value
        }

    val isCachingEnabled: Boolean
        get() = arguments.getBoolean(EXTRA_CACHING_ENABLED)

    override fun onItemClick(position: Int, holder: RecyclerView.ViewHolder) {
        val topic = adapter.getTopic(position)
        val intent = Intent(activity, TopicChatActivity::class.java)
        intent.putExtra(EXTRA_ACCOUNT, account)
        intent.putExtra(EXTRA_TOPIC, topic)
        startActivity(intent)
    }

    private val account: Account
        get() = arguments.getParcelable<Account>(EXTRA_ACCOUNT)

    override fun getActionIcon(): Int {
        return R.drawable.ic_action_edit
    }

    override fun onActionPerformed() {
        val df = NewTopicTypeDialogFragment()
        df.arguments = Bundle {
            putParcelable(EXTRA_ACCOUNT, account)
        }
        df.show(childFragmentManager, "new_topic_type")
    }

    override fun getActionMenuFragment(): Class<out FloatingActionMenuFragment>? {
        return TopicsMenuFragment::class.java
    }

    override fun onLoadMoreContents(@IndicatorPosition position: Int) {
        // Only supports load from end, skip START flag
        if (position and IndicatorPosition.START != 0) return
        super.onLoadMoreContents(position)
        val loaderArgs = Bundle {
            putBoolean(EXTRA_READ_CACHE, false)
            putBoolean(EXTRA_READ_OLD, true)
        }
        val adapter = adapter
        val topicsCount = adapter.topicsCount
        if (topicsCount > 0) {
            loaderArgs.putString(EXTRA_MAX_ID, adapter.getTopic(topicsCount - 1).id)
        }
        loaderManager.restartLoader(0, loaderArgs, this)
    }

    override fun onSkillClick(position: Int, holder: TopicViewHolder) {
        val intent = Intent(context, SkillUpdatesActivity::class.java)
        intent.putExtra(EXTRA_ACCOUNT, account)
        intent.putExtra(EXTRA_SKILL, adapter.getTopic(position).skill)
        startActivity(intent)
    }

    override fun onUserClick(position: Int, holder: TopicViewHolder) {
        val intent = Intent(context, UserActivity::class.java)
        intent.putExtra(EXTRA_ACCOUNT, account)
        intent.putExtra(EXTRA_USER, adapter.getTopic(position).user)
        startActivity(intent)
    }

    override fun onRelatedUsersClick(position: Int, holder: SkillTopicRelatedUsersViewHolder) {
        val intent = Intent(context, SkillUsersActivity::class.java)
        intent.putExtra(EXTRA_ACCOUNT, account)
        intent.putExtra(EXTRA_SKILL, skill)
        startActivity(intent)
    }

    override fun onMediaClick(attachments: Array<Attachment>, attachment: Attachment, clickedView: View) {
        val intent = Intent(context, MediaViewerActivity::class.java)
        intent.putExtra(EXTRA_MEDIA, attachments)
        intent.putExtra(EXTRA_CURRENT_MEDIA, attachment)
        val location = IntArray(2)
        clickedView.getLocationOnScreen(location)
        intent.sourceBounds = Rect(location[0], location[1], location[0] + clickedView.width,
                location[1] + clickedView.height)
        val options = ActivityOptionsCompat.makeScaleUpAnimation(clickedView, 0, 0,
                clickedView.width, clickedView.height).toBundle()
        ActivityCompat.startActivity(activity, intent, options)
    }

    fun reloadWithSortOrder(@TopicSortOrder sortBy: String) {
        if (TextUtils.equals(sortOrder, sortBy) || hasUserId()) return
        mSortBy = sortBy
        preferences.edit().putString(KEY_TOPICS_SORT_ORDER, sortBy).apply()
        val loaderArgs = Bundle {
            putBoolean(EXTRA_READ_CACHE, false)
            putBoolean(EXTRA_READ_OLD, false)
        }
        loaderManager.restartLoader(0, loaderArgs, this)
        showProgress()
    }

    class NewTopicTypeDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(context)
            val resources = resources
            val entries = resources.getStringArray(R.array.new_topic_type_entries)
            val values = resources.getStringArray(R.array.new_topic_type_values)
            builder.setItems(entries) { dialog, which ->
                when (values[which]) {
                    "photos_text" -> {
                        val intent = Intent(context, NewTopicActivity::class.java)
                        intent.putExtra(EXTRA_ACCOUNT, account)
                        startActivity(intent)
                    }
                    "audio" -> {
                    }
                    "location" -> {
                        val parent = parentFragment
                        val intent = Intent(context, LocationPickerActivity::class.java)
                        intent.putExtra(EXTRA_ACCOUNT, account)
                        parent.startActivityForResult(intent, REQUEST_NEW_LOCATION_TOPIC)
                    }
                }
            }
            return builder.create()
        }

    }

    companion object {

        private val REQUEST_NEW_LOCATION_TOPIC = 102
    }

    class RelatedUsersLoader(
            context: Context,
            val account: Account,
            val skill: Skill
    ) : AsyncTaskLoader<List<User>?>(context) {

        override fun loadInBackground(): List<User>? {
            val yep = YepAPIFactory.getInstance(context, account)
            try {
                return yep.getMasteredUsers(skill.id)
            } catch (e: YepException) {
                return null
            }
        }

        override fun onStartLoading() {
            forceLoad()
        }
    }

}
