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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import java.io.IOException;
import java.util.List;

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

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends SwipeBackContentActivity implements Constants, LoaderManager.LoaderCallbacks<List<Message>> {

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
//                    Realm realm = Utils.getRealmForAccount(handler, account);
//                    realm.beginTransaction();
//                    realm.copyToRealmOrUpdate(result.getData());
//                    realm.commitTransaction();
                }
                super.callback(handler, result);
            }

            @Override
            public TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
                YepAPI yep = YepAPIFactory.getInstance(ChatActivity.this, account);
                try {
                    final Message message = yep.createMessage(newMessage);
                    message.setConversationId(conversation.getId());
                    message.setOutgoing(true);
                    return TaskResponse.getInstance(message);
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }
        };
        final NewMessage newMessage = new NewMessage();
        newMessage.textContent(String.valueOf(mEditText.getText()));
        newMessage.recipientId(conversation.getId());
        newMessage.recipientType(conversation.getRecipientType());
        task.setParams(newMessage);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
        mEditText.setText("");
    }

    @Override
    public Loader<List<Message>> onCreateLoader(final int id, final Bundle args) {
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public void onLoadFinished(final Loader<List<Message>> loader, final List<Message> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(final Loader<List<Message>> loader) {
        mAdapter.setData(null);
    }

    private static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_MESSAGE_INCOMING = 1;
        private static final int VIEW_TYPE_MESSAGE_OUTGOING = 2;
        private final LayoutInflater mInflater;
        private List<Message> mData;

        ChatAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_MESSAGE_INCOMING: {
                    return new IncomingChatViewHolder(mInflater.inflate(R.layout.list_item_message_incoming, parent, false));
                }
                case VIEW_TYPE_MESSAGE_OUTGOING: {
                    return new OutgoingChatViewHolder(mInflater.inflate(R.layout.list_item_message_outgoing, parent, false));
                }
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MessageViewHolder) holder).displayMessage(mData.get(position));
        }

        @Override
        public int getItemViewType(final int position) {
            if (mData.get(position).isOutgoing()) return VIEW_TYPE_MESSAGE_OUTGOING;
            return VIEW_TYPE_MESSAGE_INCOMING;
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        public void setData(final List<Message> data) {
            mData = data;
            notifyDataSetChanged();
        }

        private static abstract class MessageViewHolder extends RecyclerView.ViewHolder {

            public MessageViewHolder(final View itemView) {
                super(itemView);
            }

            public abstract void displayMessage(Message message);
        }

        private static class IncomingChatViewHolder extends MessageViewHolder {
            public IncomingChatViewHolder(final View itemView) {
                super(itemView);
            }

            @Override
            public void displayMessage(Message message) {
                final TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
                text1.setText(message.getTextContent());
            }
        }

        private static class OutgoingChatViewHolder extends MessageViewHolder {
            public OutgoingChatViewHolder(final View itemView) {
                super(itemView);
            }

            @Override
            public void displayMessage(Message message) {
                final TextView text1 = (TextView) itemView.findViewById(android.R.id.text1);
                text1.setText(message.getTextContent());
            }
        }
    }
}
