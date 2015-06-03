/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.loader.MessagesLoader;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.TintedStatusFrameLayout;
import io.realm.RealmResults;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends SwipeBackContentActivity implements Constants, LoaderManager.LoaderCallbacks<RealmResults<Message>> {

    private RecyclerView mRecyclerView;
    private TintedStatusFrameLayout mMainContent;


    private FixedLinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));

        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mLayoutManager = new FixedLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.setStackFromEnd(true);
        mAdapter = new ChatAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_CONVERSATION)) {
            finish();
            return;
        }
        getSupportLoaderManager().initLoader(0, intent.getExtras(), this);
    }

    @Override
    public Loader<RealmResults<Message>> onCreateLoader(final int id, final Bundle args) {
        final Conversation conversation;
        try {
            conversation = LoganSquare.parse(args.getString(EXTRA_CONVERSATION), Conversation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new MessagesLoader(this, Utils.getCurrentAccount(this), conversation);
    }

    @Override
    public void onLoadFinished(final Loader<RealmResults<Message>> loader, final RealmResults<Message> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(final Loader<RealmResults<Message>> loader) {
        mAdapter.setData(null);
    }

    private static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final LayoutInflater mInflater;
        private RealmResults<Message> mData;

        ChatAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChatViewHolder(mInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ChatViewHolder) holder).displayMessage(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public void setData(final RealmResults<Message> data) {
            mData = data;
            notifyDataSetChanged();
        }

        private static class ChatViewHolder extends RecyclerView.ViewHolder {
            public ChatViewHolder(final View itemView) {
                super(itemView);
            }

            public void displayMessage(Message message) {
                final TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
                text1.setText(message.getTextContent());
            }
        }
    }
}
