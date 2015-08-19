/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.sqliteqb.library.Expression;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.loader.MessagesLoader;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.TaskResponse;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.ContentValuesCreator;
import catchla.yep.util.EditTextEnterHandler;
import catchla.yep.util.ParseUtils;
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

    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_TAKE_PHOTO = 102;
    private RecyclerView mRecyclerView;
    private TintedStatusFrameLayout mMainContent;
    private ImageView mAttachSendButton;
    private FixedLinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;
    private EditText mEditText;
    private Conversation mConversation;
    private PopupMenu mAttachPopupMenu;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mEditText = (EditText) findViewById(R.id.edit_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mAttachSendButton = (ImageView) findViewById(R.id.attachment_send);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
            case REQUEST_TAKE_PHOTO: {
                if (resultCode != RESULT_OK) return;
                final Uri imageUri = data.getData();
                sendMessage(new SendMessageHandler() {
                    @Override
                    public void beforeSend(final YepAPI yep, final NewMessage message) throws YepException {
                        final S3UploadToken token = yep.getS3UploadToken();
                        final RestHttpClient client = YepAPIFactory.getHttpClient(yep);
                        try {
                            Utils.uploadToS3(client, token, new File(imageUri.getPath()));
                        } catch (IOException e) {
                            throw new YepException(e);
                        }
                        message.attachment(new NewMessage.ImageAttachment(token));
                    }
                });
                return;
            }
        }
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
        mLayoutManager = new FixedLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mLayoutManager.setStackFromEnd(false);
        mAdapter = new ChatAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAttachSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mEditText.length() > 0) {
                    sendTextMessage();
                } else {
                    openAttachmentMenu();
                }
            }
        });

        final EditTextEnterHandler handler = EditTextEnterHandler.attach(mEditText, new EditTextEnterHandler.EnterListener() {
            @Override
            public void onHitEnter() {
                sendTextMessage();
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
        mAttachPopupMenu = new PopupMenu(mAttachSendButton.getContext(), mAttachSendButton);
        mAttachPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gallery: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(ChatActivity.this).pickImage().build(), REQUEST_PICK_IMAGE);
                        return true;
                    }
                    case R.id.camera: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(ChatActivity.this).takePhoto().build(), REQUEST_TAKE_PHOTO);
                        return true;
                    }
                    case R.id.location: {
                        sendLocation();
                        return true;
                    }
                }
                return false;
            }
        });
        mAttachPopupMenu.inflate(R.menu.action_attach_send);
        getSupportLoaderManager().initLoader(0, intent.getExtras(), this);
    }

    private void sendLocation() {
        sendMessage(new SendMessageHandler() {
            @Override
            public void beforeSend(final YepAPI yep, final NewMessage message) throws YepException {
                Location location = Utils.getCachedLocation(ChatActivity.this);
                if (location != null) {
                    message.location(location.getLatitude(), location.getLongitude());
                    message.mediaType(Message.MediaType.LOCATION);
                }
            }
        });
    }

    private void sendTextMessage() {
        sendMessage(new SendMessageHandler() {
            @Override
            public void beforeSend(final YepAPI yep, final NewMessage message) throws YepException {
                message.mediaType(Message.MediaType.TEXT);
            }
        });
    }

    private void openAttachmentMenu() {
        mAttachPopupMenu.show();
    }

    interface SendMessageHandler {
        void beforeSend(YepAPI yep, NewMessage message) throws YepException;
    }

    private void sendMessage(final SendMessageHandler sendMessageHandler) {
        final Account account = Utils.getCurrentAccount(ChatActivity.this);
        final Conversation conversation = mConversation;
        if (account == null || conversation == null) return;
        final TaskRunnable<NewMessage, TaskResponse<Message>, ChatActivity> task = new TaskRunnable<NewMessage, TaskResponse<Message>, ChatActivity>() {
            @Override
            public void callback(final ChatActivity handler, final TaskResponse<Message> result) {
                if (result.hasData()) {
                    getSupportLoaderManager().restartLoader(0, getIntent().getExtras(), ChatActivity.this);
                }
                super.callback(handler, result);
            }

            @Override
            public TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
                YepAPI yep = YepAPIFactory.getInstance(ChatActivity.this, account);
                final ContentResolver cr = getContentResolver();
                ContentValues values = ContentValuesCreator.fromNewMessage(newMessage);
                values.put(Messages.STATE, Messages.MessageState.SENDING);
                values.put(Messages.OUTGOING, true);
                final long rowId = ParseUtils.parseLong(cr.insert(Messages.CONTENT_URI, values).getLastPathSegment());
                TaskResponse<Message> response;
                try {
                    sendMessageHandler.beforeSend(yep, newMessage);
                    values = ContentValuesCreator.fromNewMessage(newMessage);
                    final Message message = yep.createMessage(newMessage);
                    values.put(Messages.MESSAGE_ID, message.getId());
                    values.put(Messages.STATE, Messages.MessageState.SENT);
                    final long createdAt = Utils.getTime(message.getCreatedAt());
                    values.put(Messages.CREATED_AT, createdAt);
                    final ContentValues conversationValues = ContentValuesCreator.fromConversation(conversation);
                    conversationValues.put(Conversations.TEXT_CONTENT, message.getTextContent());
                    conversationValues.put(Conversations.UPDATED_AT, createdAt);
                    cr.insert(Conversations.CONTENT_URI, conversationValues);
                    response = TaskResponse.getInstance(message);
                } catch (YepException e) {
                    values.put(Messages.STATE, Messages.MessageState.FAILED);
                    Log.w(LOGTAG, e);
                    response = TaskResponse.getInstance(e);
                }
                cr.update(Messages.CONTENT_URI, values, Expression.equals(Messages._ID, rowId).getSQL(),
                        null);
                return response;
            }
        };
        final NewMessage newMessage = new NewMessage();
        newMessage.textContent(String.valueOf(mEditText.getText()));
        newMessage.conversationId(conversation.getId());
        newMessage.recipientId(conversation.getRecipientId());
        newMessage.recipientType(conversation.getRecipientType());
        newMessage.circle(conversation.getCircle());
        newMessage.sender(conversation.getSender());
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

        private static final int FLAG_MESSAGE_OUTGOING = 0xF0000000;
        private static final int VIEW_SUBTYPE_MESSAGE_TEXT = 0x0001;
        private static final int VIEW_SUBTYPE_MESSAGE_LOCATION = 0x0002;
        private final LayoutInflater mInflater;
        private List<Message> mData;

        ChatAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final boolean isOutgoing = (viewType & FLAG_MESSAGE_OUTGOING) != 0;
            final View baseView;
            if (isOutgoing) {
                baseView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false);
            } else {
                baseView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false);
            }
            final int subType = viewType & ~FLAG_MESSAGE_OUTGOING;
            switch (subType) {
                case VIEW_SUBTYPE_MESSAGE_TEXT: {
                    return new MessageViewHolder(baseView);
                }
                case VIEW_SUBTYPE_MESSAGE_LOCATION: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_container);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_location, attachmentContainer);
                    return new LocationChatViewHolder(baseView);
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
            final Message message = mData.get(position);
            int subType = getItemViewSubType(message.getMediaType());
            if (message.isOutgoing()) return subType | FLAG_MESSAGE_OUTGOING;
            return subType;
        }

        private int getItemViewSubType(final String mediaType) {
            if (Message.MediaType.LOCATION.equals(mediaType)) return VIEW_SUBTYPE_MESSAGE_LOCATION;
            return VIEW_SUBTYPE_MESSAGE_TEXT;
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

        private static class MessageViewHolder extends RecyclerView.ViewHolder {

            private final TextView text1;

            public MessageViewHolder(final View itemView) {
                super(itemView);
                text1 = (TextView) itemView.findViewById(android.R.id.text1);
            }

            public void displayMessage(Message message) {
                text1.setText(message.getTextContent());

            }
        }

        private static class LocationChatViewHolder extends MessageViewHolder {
            private final MapView mapView;

            public LocationChatViewHolder(final View itemView) {
                super(itemView);
                mapView = (MapView) itemView.findViewById(R.id.map_view);
                mapView.setTilesScaledToDpi(true);
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        return true;
                    }
                });
            }

            @Override
            public void displayMessage(Message message) {
                super.displayMessage(message);
                final GeoPoint gp = new GeoPoint((int) (message.getLatitude() * 1E6), (int) (message.getLongitude() * 1E6));
                final IMapController mc = mapView.getController();
                mc.setZoom(12);
                mc.setCenter(gp);
            }
        }

    }
}
