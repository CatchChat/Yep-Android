/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;

import java.util.List;

import catchla.yep.R;
import catchla.yep.activity.FindFriendActivity;
import catchla.yep.activity.UserActivity;
import catchla.yep.adapter.FriendsListAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.loader.FriendshipsLoader;
import catchla.yep.message.FriendshipsRefreshedEvent;
import catchla.yep.model.Friendship;
import catchla.yep.service.MessageService;

/**
 * Created by mariotaku on 15/4/29.
 */
public class FriendsListFragment extends AbsContentListRecyclerViewFragment<FriendsListAdapter>
        implements LoaderManager.LoaderCallbacks<List<Friendship>>, IActionButtonSupportFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);
        showContent();
        getAdapter().setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position, final RecyclerView.ViewHolder holder) {
                final Friendship friendship = getAdapter().getFriendship(position);
                final Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_USER, friendship.getFriend());
                startActivity(intent);
            }
        });
    }

    private Account getAccount() {
        return getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @NonNull
    @Override
    protected FriendsListAdapter onCreateAdapter(Context context) {
        return new FriendsListAdapter(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_friends_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }


    @Override
    public Loader<List<Friendship>> onCreateLoader(final int id, final Bundle args) {
        return new FriendshipsLoader(getActivity(), getAccount());
    }

    @Override
    public void onLoadFinished(final Loader<List<Friendship>> loader, final List<Friendship> data) {
        getAdapter().setData(data);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMessageRefreshed(FriendshipsRefreshedEvent event) {
        setRefreshing(false);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean triggerRefresh() {
        final FragmentActivity activity = getActivity();
        final Intent intent = new Intent(activity, MessageService.class);
        intent.setAction(MessageService.ACTION_REFRESH_FRIENDSHIPS);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        activity.startService(intent);
        return true;
    }

    @Override
    public void onLoaderReset(final Loader<List<Friendship>> loader) {
        getAdapter().setData(null);
    }

    @Override
    public int getActionIcon() {
        return R.drawable.ic_action_add;
    }

    @Override
    public void onActionPerformed() {
        final Intent intent = new Intent(getActivity(), FindFriendActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        startActivity(intent);
    }

    @Nullable
    @Override
    public Class<? extends FloatingActionMenuFragment> getActionMenuFragment() {
        return null;
    }
}
