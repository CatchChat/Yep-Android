/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.squareup.otto.Subscribe;

import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ChatActivity;
import catchla.yep.adapter.ChatsListAdapter;
import catchla.yep.adapter.decorator.DividerItemDecoration;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.loader.ConversationsLoader;
import catchla.yep.message.MessageRefreshedEvent;
import catchla.yep.model.Conversation;
import catchla.yep.service.MessageService;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatsListFragment extends AbsContentRecyclerViewFragment<ChatsListAdapter> implements Constants,
        LoaderManager.LoaderCallbacks<List<Conversation>>, IActionButtonSupportFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        final Context viewContext = getActivity();

        final RecyclerView recyclerView = getRecyclerView();
        final LinearLayoutManager layoutManager = getLayoutManager();
        final DividerItemDecoration itemDecoration = new DividerItemDecoration(viewContext, layoutManager.getOrientation());
        final Resources res = viewContext.getResources();
        final int decorPaddingLeft = res.getDimensionPixelSize(R.dimen.element_spacing_normal) * 2
                + res.getDimensionPixelSize(R.dimen.icon_size_status_profile_image);
        itemDecoration.setPadding(decorPaddingLeft, 0, 0, 0);
        recyclerView.addItemDecoration(itemDecoration);
        getAdapter().setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position, final RecyclerView.ViewHolder holder) {
                final Conversation conversation = getAdapter().getConversation(position);
                final Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_CONVERSATION, JsonSerializer.serialize(conversation, Conversation.class));
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        showProgress();
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
    public void onMessageRefreshed(MessageRefreshedEvent event) {
        setRefreshing(false);
        getLoaderManager().restartLoader(0, null, this);
    }

    @NonNull
    @Override
    protected ChatsListAdapter onCreateAdapter(Context context) {
        return new ChatsListAdapter(this, context);
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
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public Loader<List<Conversation>> onCreateLoader(final int id, final Bundle args) {
        return new ConversationsLoader(getActivity(), getAccount());
    }

    @Override
    public void onLoadFinished(final Loader<List<Conversation>> loader, final List<Conversation> data) {
        getAdapter().setData(data);
        showContent();
    }

    @Override
    public void onLoaderReset(final Loader<List<Conversation>> loader) {
        getAdapter().setData(null);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        final FragmentActivity activity = getActivity();
        final Intent intent = new Intent(activity, MessageService.class);
        intent.setAction(MessageService.ACTION_REFRESH_MESSAGES);
        intent.putExtra(EXTRA_ACCOUNT, getAccount());
        activity.startService(intent);
    }

    private Account getAccount() {
        return getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @Override
    public int getActionIcon() {
        return R.drawable.ic_action_search;
    }

    @Override
    public void onActionPerformed() {

    }
}
