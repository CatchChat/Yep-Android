package catchla.yep.fragment;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.List;

import catchla.yep.R;
import catchla.yep.activity.LocationPickerActivity;
import catchla.yep.activity.MediaViewerActivity;
import catchla.yep.activity.NewTopicActivity;
import catchla.yep.activity.SkillUpdatesActivity;
import catchla.yep.activity.TopicChatActivity;
import catchla.yep.activity.UserActivity;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.loader.DiscoverTopicsLoader;
import catchla.yep.model.Attachment;
import catchla.yep.model.LocationAttachment;
import catchla.yep.model.Paging;
import catchla.yep.model.Topic;
import catchla.yep.view.holder.TopicViewHolder;

/**
 * Created by mariotaku on 15/10/12.
 */
public class TopicsListFragment extends AbsContentListRecyclerViewFragment<TopicsAdapter>
        implements LoaderManager.LoaderCallbacks<List<Topic>>, TopicsAdapter.TopicClickListener,
        IActionButtonSupportFragment {

    private static final int REQUEST_NEW_LOCATION_TOPIC = 102;

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
    public Loader<List<Topic>> onCreateLoader(final int id, final Bundle args) {
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
    public void onLoadFinished(final Loader<List<Topic>> loader, final List<Topic> data) {
        final TopicsAdapter adapter = getAdapter();
        adapter.setData(data);
        adapter.setLoadMoreSupportedPosition(data != null && !data.isEmpty() ? IndicatorPosition.END : IndicatorPosition.NONE);
        showContent();
        setRefreshing(false);
        setRefreshEnabled(true);
        setLoadMoreIndicatorPosition(IndicatorPosition.NONE);
    }

    @Override
    public void onLoaderReset(final Loader<List<Topic>> loader) {

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_NEW_LOCATION_TOPIC: {
                if (resultCode == Activity.RESULT_OK) {
                    final String name = data.getStringExtra(EXTRA_NAME);
                    final Location location = data.getParcelableExtra(EXTRA_LOCATION);
                    final Intent intent = new Intent(getContext(), NewTopicActivity.class);
                    final LocationAttachment attachment = new LocationAttachment();
                    attachment.setPlace(name);
                    attachment.setLatitude(location.getLatitude());
                    attachment.setLongitude(location.getLongitude());
                    intent.putExtra(EXTRA_NEW_TOPIC_TYPE, NewTopicActivity.TYPE_LOCATION);
                    intent.putExtra(EXTRA_ATTACHMENT, attachment);
                    intent.putExtra(EXTRA_ACCOUNT, getAccount());
                    startActivity(intent);
                    return;
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACCOUNT, getAccount());
        final NewTopicTypeDialogFragment df = new NewTopicTypeDialogFragment();
        df.setArguments(args);
        df.show(getChildFragmentManager(), "new_topic_type");
    }

    @Nullable
    @Override
    public Class<? extends FloatingActionMenuFragment> getActionMenuFragment() {
        return TopicsMenuFragment.class;
    }

    @Override
    public void onLoadMoreContents(@IndicatorPosition final int position) {
        // Only supports load from end, skip START flag
        if ((position & IndicatorPosition.START) != 0) return;
        super.onLoadMoreContents(position);
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

    @Override
    public void onUserClick(final int position, final TopicViewHolder holder) {
        final Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        intent.putExtra(EXTRA_USER, getAdapter().getTopic(position).getUser());
        startActivity(intent);
    }

    @Override
    public void onMediaClick(final Attachment[] attachments, final Attachment attachment, View view) {
        final Intent intent = new Intent(getContext(), MediaViewerActivity.class);
        intent.putExtra(EXTRA_MEDIA, attachments);
        intent.putExtra(EXTRA_CURRENT_MEDIA, attachment);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        intent.setSourceBounds(new Rect(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight()));
        final Bundle options = ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0,
                view.getWidth(), view.getHeight()).toBundle();
        ActivityCompat.startActivity(getActivity(), intent, options);
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

    public static class NewTopicTypeDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final Resources resources = getResources();
            final String[] entries = resources.getStringArray(R.array.new_topic_type_entries);
            final String[] values = resources.getStringArray(R.array.new_topic_type_values);
            builder.setItems(entries, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    switch (values[which]) {
                        case "photos_text": {
                            final Intent intent = new Intent(getContext(), NewTopicActivity.class);
                            intent.putExtra(EXTRA_ACCOUNT, getAccount());
                            startActivity(intent);
                            break;
                        }
                        case "audio": {
                            break;
                        }
                        case "location": {
                            final Fragment parent = getParentFragment();
                            final Intent intent = new Intent(getContext(), LocationPickerActivity.class);
                            intent.putExtra(EXTRA_ACCOUNT, getAccount());
                            parent.startActivityForResult(intent, REQUEST_NEW_LOCATION_TOPIC);
                            break;
                        }
                    }
                }
            });
            return builder.create();
        }

        private Account getAccount() {
            return getArguments().getParcelable(EXTRA_ACCOUNT);
        }
    }
}
