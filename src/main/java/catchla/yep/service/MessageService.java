package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.PersistedTaskRunnable;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.otto.Bus;

import org.mariotaku.sqliteqb.library.ArgsArray;
import org.mariotaku.sqliteqb.library.Columns;
import org.mariotaku.sqliteqb.library.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.message.FriendshipsRefreshedEvent;
import catchla.yep.message.MessageRefreshedEvent;
import catchla.yep.model.Circle;
import catchla.yep.model.CircleCursorIndices;
import catchla.yep.model.Conversation;
import catchla.yep.model.Friendship;
import catchla.yep.model.Message;
import catchla.yep.model.Paging;
import catchla.yep.model.ResponseList;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.provider.YepDataStore.Circles;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.ContentResolverUtils;
import catchla.yep.util.ContentValuesCreator;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.dagger.GeneralComponentHelper;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessageService extends Service implements Constants {

    public static final String ACTION_PREFIX = BuildConfig.APPLICATION_ID + ".";
    public static final String ACTION_REFRESH_MESSAGES = ACTION_PREFIX + "REFRESH_MESSAGES";
    public static final String ACTION_REFRESH_USER_INFO = ACTION_PREFIX + "REFRESH_USER_INFO";
    public static final String ACTION_REFRESH_FRIENDSHIPS = ACTION_PREFIX + "REFRESH_FRIENDSHIPS";

    @Inject
    Bus mBus;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GeneralComponentHelper.build(this).inject(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent == null) return START_NOT_STICKY;
        final String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;
        switch (action) {
            case ACTION_REFRESH_FRIENDSHIPS: {
                final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
                refreshFriendships(account);
                break;
            }
            case ACTION_REFRESH_MESSAGES: {
                refreshCircles();
                refreshMessages();
                break;
            }
            case ACTION_REFRESH_USER_INFO: {
                final Account account = intent.getParcelableExtra(EXTRA_ACCOUNT);
                refreshUserInfo(account);
                break;
            }
        }
        return START_STICKY;
    }

    private void refreshUserInfo(final Account account) {
        if (account == null) return;

        final TaskRunnable<Account, TaskResponse<User>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<User>, MessageService>() {

            @Override
            public TaskResponse<User> doLongOperation(final Account account) throws InterruptedException {
                YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    return TaskResponse.getInstance(yep.getUser());
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final MessageService handler, final TaskResponse<User> result) {
                if (result.hasData()) {
                    Utils.saveUserInfo(handler, account, result.getData());
                }
            }
        };
        AsyncManager.runBackgroundTask(task);
    }

    private void refreshFriendships(final Account account) {
        if (account == null) return;
        final TaskRunnable<Account, TaskResponse<Boolean>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<Boolean>, MessageService>() {
            @Override
            public TaskResponse<Boolean> doLongOperation(final Account account) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    ResponseList<Friendship> friendships;
                    int page = 1;
                    final Paging paging = new Paging();
                    final ArrayList<ContentValues> values = new ArrayList<>();
                    final String accountId = Utils.getAccountId(getApplication(), account);
                    while ((friendships = yep.getFriendships(paging)).size() > 0) {
                        for (Friendship friendship : friendships) {
                            values.add(ContentValuesCreator.fromFriendship(friendship, accountId));
                        }
                        paging.page(++page);
                        if (friendships.getCount() < friendships.getPerPage()) break;
                    }

                    final ContentResolver cr = getContentResolver();
                    cr.delete(Friendships.CONTENT_URI, null, null);
                    ContentResolverUtils.bulkInsert(cr, Friendships.CONTENT_URI, values);
                    return TaskResponse.getInstance(true);
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                    return TaskResponse.getInstance(e);
                } finally {
                }
            }

            @Override
            public void callback(final TaskResponse<Boolean> response) {
                mBus.post(new FriendshipsRefreshedEvent());
            }

            @Override
            public void callback(final MessageService messageService, final TaskResponse<Boolean> response) {
                callback(response);
            }
        };
        task.setParams(account);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private void refreshMessages() {
        final Account account = Utils.getCurrentAccount(this);
        if (account == null) return;
        final User accountUser = Utils.getAccountUser(this, account);
        if (accountUser == null) return;
        final TaskRunnable<Account, TaskResponse<Boolean>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<Boolean>, MessageService>() {
            @Override
            public TaskResponse<Boolean> doLongOperation(final Account account) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    ResponseList<Message> messages = yep.getUnreadMessages();
                    final String accountId = accountUser.getId();
                    insertMessages(MessageService.this, messages, accountId);
                    return TaskResponse.getInstance(true);
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                    return TaskResponse.getInstance(e);
                } catch (Throwable e) {
                    Log.wtf(LOGTAG, e);
                    return TaskResponse.getInstance(e);
                } finally {
                }
            }

            @Override
            public void callback(final TaskResponse<Boolean> response) {
                mBus.post(new MessageRefreshedEvent());
            }

            @Override
            public void callback(final MessageService messageService, final TaskResponse<Boolean> response) {
                callback(response);
            }
        };
        task.setParams(account);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private void refreshCircles() {
        final Account account = Utils.getCurrentAccount(this);
        if (account == null) return;
        final User accountUser = Utils.getAccountUser(this, account);
        if (accountUser == null) return;
        final TaskRunnable<Account, TaskResponse<Boolean>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<Boolean>, MessageService>() {
            @Override
            public TaskResponse<Boolean> doLongOperation(final Account account) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    Paging paging = new Paging();
                    ResponseList<Circle> circles = yep.getCircles(paging);
                    final String accountId = accountUser.getId();
                    insertCircles(MessageService.this, circles, accountId);
                    return TaskResponse.getInstance(true);
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                    return TaskResponse.getInstance(e);
                } finally {
                }
            }

            @Override
            public void callback(final TaskResponse<Boolean> response) {
                mBus.post(new MessageRefreshedEvent());
            }

            @Override
            public void callback(final MessageService messageService, final TaskResponse<Boolean> response) {
                callback(response);
            }
        };
        task.setParams(account);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    public static void insertCircles(final Context context, final Collection<Circle> circles, final String accountId) {
        final ContentResolver cr = context.getContentResolver();
        cr.delete(Circles.CONTENT_URI, null, null);
        final List<ContentValues> contentValues = new ArrayList<>();
        for (final Circle circle : circles) {
            contentValues.add(ContentValuesCreator.fromCircle(circle, accountId));
        }
        ContentResolverUtils.bulkInsert(cr, Circles.CONTENT_URI, contentValues);
    }

    public static void insertMessages(final Context context, final Collection<Message> messages, final String accountId) {
        HashMap<String, ContentValues> conversations = new HashMap<>();
        final ContentResolver cr = context.getContentResolver();
        final Set<String> ids = new HashSet<>();
        for (Message message : messages) {
            ids.add(message.getId());
            final String recipientType = message.getRecipientType();
            final String conversationId = Conversation.generateId(message);
            message.setConversationId(conversationId);
            message.setOutgoing(false);

            ContentValues conversation = conversations.get(conversationId);
            final boolean newConversation = conversation == null;
            if (conversation == null) {
                conversation = new ContentValues();
                conversation.put(Conversations.ACCOUNT_ID, accountId);
                conversation.put(Conversations.CONVERSATION_ID, conversationId);
            }
            final long createdAt = Utils.getTime(message.getCreatedAt());
            if (newConversation || createdAt > conversation.getAsLong(Conversations.UPDATED_AT)) {
                conversation.put(Conversations.TEXT_CONTENT, message.getTextContent());
                final String senderString = JsonSerializer.serialize(message.getSender(), User.class);
                conversation.put(Conversations.USER, senderString);
                conversation.put(Conversations.SENDER, senderString);
                conversation.put(Conversations.CIRCLE, getMessageCircle(context, message, accountId));
                conversation.put(Conversations.UPDATED_AT, createdAt);
                conversation.put(Conversations.RECIPIENT_TYPE, recipientType);
                conversation.put(Conversations.MEDIA_TYPE, message.getMediaType());
            }
            if (newConversation) {
                conversations.put(conversationId, conversation);
            }
        }

        final int idsSize = ids.size();
        final String[] selectionArgs = new String[idsSize + 1];
        selectionArgs[0] = accountId;
        System.arraycopy(ids.toArray(new String[idsSize]), 0, selectionArgs, 1, idsSize);
        cr.delete(Messages.CONTENT_URI, Expression.and(Expression.equalsArgs(Messages.ACCOUNT_ID),
                        Expression.in(new Columns.Column(Messages.MESSAGE_ID), new ArgsArray(idsSize))).getSQL(),
                selectionArgs);
        final ArrayList<ContentValues> messagesValues = new ArrayList<>();
        for (Message message : messages) {
            messagesValues.add(ContentValuesCreator.fromMessage(message, accountId));
        }
        ContentResolverUtils.bulkInsert(cr, Messages.CONTENT_URI, messagesValues);
        ContentResolverUtils.bulkInsert(cr, Conversations.CONTENT_URI, conversations.values());
    }

    private static String getMessageCircle(final Context context, final Message message, final String accountId) {
        if (!Message.RecipientType.CIRCLE.equals(message.getRecipientType())) return null;
        Circle circle = message.getCircle();
        // First try to load from database
        final String circleId;
        if (circle == null) {
            circleId = message.getRecipientId();
        } else if (circle.getTopic() == null) {
            circleId = circle.getId();
        } else {
            return JsonSerializer.serialize(circle, Circle.class);
        }
        final String where = Expression.and(Expression.equalsArgs(Circles.ACCOUNT_ID),
                Expression.equalsArgs(Circles.CIRCLE_ID)).getSQL();
        String[] whereArgs = {accountId, circleId};
        final Cursor c = context.getContentResolver().query(Circles.CONTENT_URI,
                Circles.COLUMNS, where, whereArgs, null);
        try {
            if (c != null && c.moveToFirst()) {
                final CircleCursorIndices ci = new CircleCursorIndices(c);
                circle = ci.newObject(c);
            }
        } finally {
            Utils.closeSilently(c);
        }
        return JsonSerializer.serialize(circle, Circle.class);
    }
}