/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.loader.MessagesLoader;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.EditTextEnterHandler;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import catchla.yep.view.TintedStatusFrameLayout;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends SwipeBackContentActivity implements Constants, LoaderManager.LoaderCallbacks<RealmResults<Message>> {

    private RecyclerView mRecyclerView;
    private TintedStatusFrameLayout mMainContent;
    private ImageView mAttachSendButton;
    private FixedLinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;
    private EditText mEditText;
    private Conversation mConversation;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mEditText = (EditText) findViewById(R.id.edit_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mAttachSendButton = (ImageView) findViewById(R.id.attachment_send);
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
        mAttachSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mEditText.length() > 0) {
                    sendMessage();
                } else {
                    openAttachmentMenu();
                }
            }
        });

        final EditTextEnterHandler handler = EditTextEnterHandler.attach(mEditText, new EditTextEnterHandler.EnterListener() {
            @Override
            public void onHitEnter() {
                sendMessage();
            }
        }, true);
        handler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                mAttachSendButton.setImageResource(s.length() > 0 ? R.drawable.ic_action_send : R.drawable.ic_action_attachment);
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });

        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_CONVERSATION)) {
            finish();
            return;
        }
        getSupportLoaderManager().initLoader(0, intent.getExtras(), this);
    }

    private void openAttachmentMenu() {

    }

    private void sendMessage() {
        final Account account = Utils.getCurrentAccount(ChatActivity.this);
        final Conversation conversation = mConversation;
        if (account == null || conversation == null) return;
        final TaskRunnable<NewMessage, TaskResponse<Message>, ChatActivity> task = new TaskRunnable<NewMessage, TaskResponse<Message>, ChatActivity>() {
            @Override
            public void callback(final ChatActivity handler, final TaskResponse<Message> result) {
                if (result.hasData()) {
                    Realm realm = Utils.getRealmForAccount(handler, account);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(result.getData());
                    realm.commitTransaction();
                }
                super.callback(handler, result);
            }

            @Override
            public TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
                YepAPI yep = YepAPIFactory.getInstance(ChatActivity.this, account);
                try {
                    final Message message = yep.createMessage(newMessage);
                    message.setConversationId(conversation.getId());
                    return TaskResponse.getInstance(message);
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }
        };
        final NewMessage newMessage = new NewMessage();
        newMessage.textContent(String.valueOf(mEditText.getText()));
        newMessage.recipientId(conversation.getRecipientId());
        newMessage.recipientType(conversation.getRecipientType());
        task.setParams(newMessage);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
        mEditText.setText("");
    }

    @Override
    public Loader<RealmResults<Message>> onCreateLoader(final int id, final Bundle args) {
        final Conversation conversation;
        try {
            conversation = LoganSquare.parse(args.getString(EXTRA_CONVERSATION), Conversation.class);
            mConversation = conversation;
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
