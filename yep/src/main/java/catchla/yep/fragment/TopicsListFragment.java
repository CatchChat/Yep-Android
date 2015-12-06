package catchla.yep.fragment;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.List;

import catchla.yep.R;
import catchla.yep.activity.NewTopicActivity;
import catchla.yep.activity.SkillUpdatesActivity;
import catchla.yep.activity.TopicChatActivity;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.loader.DiscoverTopicsLoader;
import catchla.yep.model.Paging;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.Topic;
import catchla.yep.view.holder.TopicViewHolder;

/**
 * Created by mariotaku on 15/10/12.
 */
public class TopicsListFragment extends AbsContentListRecyclerViewFragment<TopicsAdapter>
        implements LoaderManager.LoaderCallbacks<TaskResponse<List<Topic>>>, TopicsAdapter.TopicClickAdapter,
        IActionButtonSupportFragment {

    private String mSortBy;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSortBy = mPreferences.getString(KEY_TOPICS_SORT_ORDER, Topic.SortOrder.TIME);
        setHasOptionsMenu(true);
        final Bundle fragmentArgs = getArguments();
        final Bundle loaderArgs = new Bundle();
        if (fragmentArgs != null) {
            loaderArgs.putBoolean(EXTRA_READ_CACHE, !fragmentArgs.containsKey(EXTRA_LEARNING)
                    && !fragmentArgs.containsKey(EXTRA_MASTER));
            if (fragmentArgs.containsKey(EXTRA_USER_ID)) {
                loaderArgs.putString(EXTRA_USER_ID, fragmentArgs.getString(EXTRA_USER_ID));
            }
        } else {
            loaderArgs.putBoolean(EXTRA_READ_CACHE, true);
        }
        getLoaderManager().initLoader(0, loaderArgs, this);
        getAdapter().setClickListener(this);
        showProgress();
    }

    @Override
    public Loader<TaskResponse<List<Topic>>> onCreateLoader(final int id, final Bundle args) {
        final boolean cachingEnabled = isCachingEnabled();
        final boolean readCache = args.getBoolean(EXTRA_READ_CACHE) && cachingEnabled;
        final boolean readOld = args.getBoolean(EXTRA_READ_OLD, readCache) && cachingEnabled;
        final String maxId = args.getString(EXTRA_MAX_ID);
        final Paging paging = new Paging();
        if (maxId != null) {
            paging.maxId(maxId);
        }
        final List<Topic> oldData;
        if (readOld) {
            oldData = getAdapter().getTopics();
        } else {
            oldData = null;
        }
        return new DiscoverTopicsLoader(getActivity(), getAccount(), getArguments().getString(EXTRA_USER_ID),
                paging, getSortOrder(), readCache, cachingEnabled, oldData);
    }

    @Topic.SortOrder
    private String getSortOrder() {
        if (hasUserId()) return Topic.SortOrder.TIME;
        return mSortBy != null ? mSortBy : Topic.SortOrder.TIME;
    }

    private boolean hasUserId() {
        final Bundle fragmentArgs = getArguments();
        return fragmentArgs != null && fragmentArgs.containsKey(EXTRA_USER_ID);
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<List<Topic>>> loader, final TaskResponse<List<Topic>> data) {
        final TopicsAdapter adapter = getAdapter();
        final List<Topic> list = data.getData();
        adapter.setData(list);
        adapter.setLoadMoreSupported(list != null && !list.isEmpty());
        showContent();
        setRefreshing(false);
        setRefreshEnabled(true);
        setLoadMoreIndicatorVisible(false);
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<List<Topic>>> loader) {

    }

    @NonNull
    @Override
    protected TopicsAdapter onCreateAdapter(Context context) {
        return new TopicsAdapter(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_chats_list, menu);
    }

    @Override
    public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRefresh() {
        final Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(EXTRA_READ_CACHE, false);
        getLoaderManager().restartLoader(0, loaderArgs, this);
    }

    @Override
    public boolean isRefreshing() {
        return getLoaderManager().hasRunningLoaders();
    }

    public boolean isCachingEnabled() {
        return getArguments().getBoolean(EXTRA_CACHING_ENABLED);
    }

    @Override
    public void onItemClick(final int position, final RecyclerView.ViewHolder holder) {
        final Topic topic = getAdapter().getTopic(position);
        final Intent intent = new Intent(getActivity(), TopicChatActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        intent.putExtra(EXTRA_TOPIC, topic);
        startActivity(intent);
    }

    private Account getAccount() {
        return getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @Override
    public int getActionIcon() {
        return R.drawable.ic_action_edit;
    }

    @Override
    public void onActionPerformed() {
        final Intent intent = new Intent(getContext(), NewTopicActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        startActivity(intent);
    }

    @Nullable
    @Override
    public Class<? extends FloatingActionMenuFragment> getActionMenuFragment() {
        return TopicsMenuFragment.class;
    }

    @Override
    public void onLoadMoreContents() {
        super.onLoadMoreContents();
        final Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(EXTRA_READ_CACHE, false);
        loaderArgs.putBoolean(EXTRA_READ_OLD, true);
        final TopicsAdapter adapter = getAdapter();
        final int topicsCount = adapter.getTopicsCount();
        if (topicsCount > 0) {
            loaderArgs.putString(EXTRA_MAX_ID, adapter.getTopic(topicsCount - 1).getId());
        }
        getLoaderManager().restartLoader(0, loaderArgs, this);
    }

    @Override
    public void onSkillClick(final int position, final TopicViewHolder holder) {
        final Intent intent = new Intent(getContext(), SkillUpdatesActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        intent.putExtra(EXTRA_SKILL, getAdapter().getTopic(position).getSkill());
        startActivity(intent);
    }

    public void reloadWithSortOrder(final String sortBy) {
        if (TextUtils.equals(getSortOrder(), sortBy) || hasUserId()) return;
        mSortBy = sortBy;
        mPreferences.edit().putString(KEY_TOPICS_SORT_ORDER, sortBy).apply();
        final Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(EXTRA_READ_CACHE, false);
        loaderArgs.putBoolean(EXTRA_READ_OLD, false);
        getLoaderManager().restartLoader(0, loaderArgs, this);
        showProgress();
    }
}
