package catchla.yep.service;

import android.accounts.Account;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.PersistedTaskRunnable;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.message.MessageRefreshedEvent;
import catchla.yep.model.Conversation;
import catchla.yep.model.Friendship;
import catchla.yep.model.Message;
import catchla.yep.model.PagedFriendships;
import catchla.yep.model.PagedMessages;
import catchla.yep.model.Paging;
import catchla.yep.model.TaskResponse;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.util.ContentResolverUtils;
import catchla.yep.util.ContentValuesCreator;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessageService extends Service implements Constants {

    public static final String ACTION_PREFIX = BuildConfig.APPLICATION_ID + ".";
    public static final String ACTION_REFRESH_MESSAGES = ACTION_PREFIX + "REFRESH_MESSAGES";
    public static final String ACTION_REFRESH_FRIENDSHIPS = ACTION_PREFIX + "REFRESH_FRIENDSHIPS";

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;
        switch (action) {
            case ACTION_REFRESH_FRIENDSHIPS: {
                refreshFriendships();
                break;
            }
            case ACTION_REFRESH_MESSAGES: {
                refreshMessages();
                break;
            }
        }
        return START_STICKY;
    }

    private void refreshFriendships() {
        final Account account = Utils.getCurrentAccount(this);
        if (account == null) return;
        final TaskRunnable<Account, TaskResponse<Boolean>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<Boolean>, MessageService>() {
            @Override
            public TaskResponse<Boolean> doLongOperation(final Account account) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    PagedFriendships friendships;
                    int page = 1;
                    final Paging paging = new Paging();
                    final ArrayList<ContentValues> values = new ArrayList<>();
                    while ((friendships = yep.getFriendships(paging)).size() > 0) {
                        for (Friendship friendship : friendships) {
                            values.add(ContentValuesCreator.fromFriendship(friendship));
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
                final Bus bus = Utils.getMessageBus();
                bus.post(new MessageRefreshedEvent());
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
        final TaskRunnable<Account, TaskResponse<Boolean>, MessageService>
                task = new PersistedTaskRunnable<Account, TaskResponse<Boolean>, MessageService>() {
            @Override
            public TaskResponse<Boolean> doLongOperation(final Account account) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(getApplication(), account);
                try {
                    PagedMessages messages;
                    int page = 1;
                    final Paging paging = new Paging();
                    HashMap<String, Conversation> conversations = new HashMap<>();
                    while ((messages = yep.getUnreadMessages(paging)).size() > 0) {
                        for (Message message : messages) {
                            final String recipientType = message.getRecipientType();
                            final String conversationId = Conversation.generateId(message);
                            message.setConversationId(conversationId);
                            message.setOutgoing(false);

                            Conversation conversation = conversations.get(conversationId);
                            if (conversation == null) {
                                conversation = new Conversation();
                                conversation.setCircle(message.getCircle());
                                conversation.setUser(message.getSender());
                                conversation.setRecipientType(recipientType);
                                conversation.setId(conversationId);
                                conversations.put(conversationId, conversation);
                            }
                            conversation.setCreatedAt(message.getCreatedAt());
                            conversation.setTextContent(message.getTextContent());
                        }

                        paging.page(++page);
                        if (messages.getCount() < messages.getPerPage()) break;
                    }
//                    realm.commitTransaction();
                    return TaskResponse.getInstance(true);
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                    return TaskResponse.getInstance(e);
                } finally {
                }
            }

            @Override
            public void callback(final TaskResponse<Boolean> response) {
                final Bus bus = Utils.getMessageBus();
                bus.post(new MessageRefreshedEvent());
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
}
