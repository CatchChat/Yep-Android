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
import android.view.View;

import com.squareup.otto.Subscribe;

import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ChatActivity;
import catchla.yep.activity.CirclesListActivity;
import catchla.yep.adapter.ChatsListAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.loader.ConversationsLoader;
import catchla.yep.message.MessageRefreshedEvent;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.service.MessageService;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ConversationsListFragment extends AbsContentListRecyclerViewFragment<ChatsListAdapter> implements Constants,
        LoaderManager.LoaderCallbacks<List<Conversation>>, IActionButtonSupportFragment {

    public static final String EXTRA_RECIPIENT_TYPE = "recipient_type";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        getAdapter().setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position, final RecyclerView.ViewHolder holder) {
                final ChatsListAdapter adapter = getAdapter();
                if (adapter.getItemViewType(position) == ChatsListAdapter.ITEM_VIEW_TYPE_CIRCLES_ENTRY) {
                    final Intent intent = new Intent(getContext(), CirclesListActivity.class);
                    intent.putExtra(EXTRA_ACCOUNT, getAccount());
                    startActivity(intent);
                    return;
                }
                final Conversation conversation = adapter.getConversation(position);
                final Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_CONVERSATION, conversation);
                startActivity(intent);
            }
        });
        final Bundle loaderArgs = new Bundle();
        loaderArgs.putString(EXTRA_RECIPIENT_TYPE, getRecipientType());
        getLoaderManager().initLoader(0, loaderArgs, this);
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
        final Bundle loaderArgs = new Bundle();
        loaderArgs.putString(EXTRA_RECIPIENT_TYPE, getRecipientType());
        getLoaderManager().restartLoader(0, loaderArgs, this);
    }

    private String getRecipientType() {
        return getArguments().getString(EXTRA_RECIPIENT_TYPE, Message.RecipientType.USER);
    }

    @NonNull
    @Override
    protected ChatsListAdapter onCreateAdapter(Context context) {
        return new ChatsListAdapter(context);
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
        final String recipientType = getRecipientType();
        return new ConversationsLoader(getActivity(), getAccount(), recipientType,
                !Message.RecipientType.CIRCLE.equals(recipientType));
    }

    @Override
    public void onLoadFinished(final Loader<List<Conversation>> loader, final List<Conversation> data) {
        getAdapter().setData(data, Message.RecipientType.CIRCLE.equals(getRecipientType()));
        showContent();
    }

    @Override
    public void onLoaderReset(final Loader<List<Conversation>> loader) {
        getAdapter().setData(null, Message.RecipientType.CIRCLE.equals(getRecipientType()));
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

    @Nullable
    @Override
    public Class<? extends FloatingActionMenuFragment> getActionMenuFragment() {
        return null;
    }
}
