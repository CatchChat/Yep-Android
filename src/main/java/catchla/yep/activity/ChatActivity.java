/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.otto.Subscribe;

import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.OrderBy;

import java.util.Date;

import catchla.yep.Constants;
import catchla.yep.IFayeService;
import catchla.yep.R;
import catchla.yep.fragment.ChatInputBarFragment;
import catchla.yep.fragment.ChatListFragment;
import catchla.yep.fragment.ConversationChatListFragment;
import catchla.yep.model.Conversation;
import catchla.yep.model.InstantStateMessage;
import catchla.yep.model.LastReadResponse;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.YepException;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.service.FayeService;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.VoiceWaveView;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends SwipeBackContentActivity implements Constants, ChatInputBarFragment.Listener, ServiceConnection {

    private TintedStatusFrameLayout mMainContent;

    private VoiceWaveView mVoiceWaveView;
    private View mVoiceWaveContainer;
    private Handler mHandler;
    private IFayeService mFayeService;


    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mVoiceWaveContainer = findViewById(R.id.voice_wave_container);
        mVoiceWaveView = (VoiceWaveView) findViewById(R.id.voice_wave_view);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
        setContentView(R.layout.activity_chat);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));

        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);

        final Conversation conversation = getConversation();
        setTitle(Utils.getConversationName(conversation));

        final Bundle fragmentArgs = new Bundle();
        fragmentArgs.putParcelable(EXTRA_CONVERSATION, conversation);
        fragmentArgs.putParcelable(EXTRA_ACCOUNT, getAccount());
        final Fragment chatListFragment = Fragment.instantiate(this,
                ConversationChatListFragment.class.getName(), fragmentArgs);
        final ChatInputBarFragment chatInputBarFragment =
                (ChatInputBarFragment) Fragment.instantiate(this,
                        ChatInputBarFragment.class.getName(), fragmentArgs);

        chatInputBarFragment.setListener(this);

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.list_container, chatListFragment);
        ft.replace(R.id.input_panel, chatInputBarFragment);
        ft.commit();

        if (savedInstanceState == null) {
            markAsRead(conversation);
        }
    }

    private Conversation getConversation() {
        return getIntent().getParcelableExtra(EXTRA_CONVERSATION);
    }

    private void markAsRead(final Conversation conversation) {
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(final Object o) throws InterruptedException {
                final String[] projection = {Messages.MESSAGE_ID};
                final Expression incomingWhere = Expression.and(
                        Expression.equalsArgs(Messages.ACCOUNT_ID),
                        Expression.equalsArgs(Messages.CONVERSATION_ID),
                        Expression.isNot(Messages.OUTGOING, 1)
                );
                final String[] whereArgs = {conversation.getAccountId(), conversation.getId()};
                final OrderBy orderBy = new OrderBy(Messages.CREATED_AT, false);
                final ContentResolver cr = getContentResolver();
                final Cursor incoming = cr.query(Messages.CONTENT_URI, projection,
                        incomingWhere.getSQL(), whereArgs, orderBy.getSQL());
                assert incoming != null;
                final String lastId;
                try {
                    if (!incoming.moveToFirst()) return null;
                    lastId = incoming.getString(0);
                } finally {
                    incoming.close();
                }
                final YepAPI yep = YepAPIFactory.getInstance(ChatActivity.this, Utils.getCurrentAccount(ChatActivity.this));
                try {
                    final String recipientType = conversation.getRecipientType();
                    final String recipientId = conversation.getRecipientId();
                    final LastReadResponse lastReadResponse;
                    switch (recipientType) {
                        case Message.RecipientType.CIRCLE: {
                            lastReadResponse = yep.getSentLastRead(YepAPI.PathRecipientType.CIRCLES, recipientId);
                            yep.batchMarkAsRead(YepAPI.PathRecipientType.CIRCLES, recipientId, lastId);
                            break;
                        }
                        case Message.RecipientType.USER: {
                            lastReadResponse = yep.getSentLastRead(YepAPI.PathRecipientType.USERS, recipientId);
                            yep.batchMarkAsRead(YepAPI.PathRecipientType.USERS, recipientId, lastId);
                            break;
                        }
                        default: {
                            throw new UnsupportedOperationException(recipientType);
                        }
                    }
                    final Date lastReadAt = lastReadResponse.getLastReadAt();
                    if (lastReadAt != null) {
                        final ContentValues values = new ContentValues();
                        values.put(Messages.STATE, Messages.MessageState.READ);
                        final Expression outgoingWhere = Expression.and(
                                Expression.equalsArgs(Messages.ACCOUNT_ID),
                                Expression.equalsArgs(Messages.CONVERSATION_ID),
                                Expression.equalsArgs(Messages.STATE),
                                Expression.lesserEqualsArgs(Messages.CREATED_AT),
                                Expression.equalsArgs(Messages.OUTGOING)
                        );
                        final String[] outgoingWhereArgs = {conversation.getAccountId(),
                                conversation.getId(), Messages.MessageState.UNREAD,
                                String.valueOf(lastReadAt.getTime()), "1"};
                        cr.update(Messages.CONTENT_URI, values, outgoingWhere.getSQL(),
                                outgoingWhereArgs);
                    }
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                } catch (Throwable t) {
                    Log.wtf(LOGTAG, t);
                }

                return null;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile: {
                final Intent intent = new Intent(this, UserActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_USER, getConversation().getUser());
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public void onRecordStarted() {
        mVoiceWaveContainer.setVisibility(View.VISIBLE);
        mVoiceWaveView.startRecording();
    }

    @WorkerThread
    @Override
    public void postSetAmplitude(final int amplitude) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVoiceWaveView.setAmplitude(amplitude);
            }
        });
    }

    @Override
    public void onRecordStopped() {
        mVoiceWaveContainer.setVisibility(View.GONE);
        mVoiceWaveView.stopRecording();
    }

    @Override
    public void onMessageSentFinished(final TaskResponse<Message> result) {

    }

    @Subscribe
    public void onReceivedInstantStateMessage(InstantStateMessage message) {
        final Conversation conversation = getConversation();
        if (Message.RecipientType.USER.equals(conversation.getRecipientType())) {
            if (TextUtils.equals(conversation.getUser().getId(), message.getUser().getId())) {
                setTitle(R.string.typing);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(Utils.getConversationName(conversation));
                    }
                }, 2000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, FayeService.class), this, BIND_AUTO_CREATE);
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        mBus.unregister(this);
        unbindService(this);
        super.onStop();
    }

    @Override
    public void onMessageSentStarted(final NewMessage newMessage) {
        final ChatListFragment chatListFragment =
                (ChatListFragment) getSupportFragmentManager().findFragmentById(R.id.list_container);
        chatListFragment.scrollToStart();
        chatListFragment.setJumpToLast(true);
    }

    @Override
    public void onTypingText() {
        if (mFayeService == null) return;
        try {
            mFayeService.instantState(getConversation(), "typing");
        } catch (RemoteException e) {
            Log.w(LOGTAG, e);
        }
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mFayeService = IFayeService.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mFayeService = null;
    }
}
