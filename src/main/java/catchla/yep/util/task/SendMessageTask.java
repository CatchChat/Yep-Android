package catchla.yep.util.task;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.desmond.asyncmanager.TaskRunnable;

import org.mariotaku.sqliteqb.library.Expression;

import catchla.yep.Constants;
import catchla.yep.model.Attachment;
import catchla.yep.model.Conversation;
import catchla.yep.model.ConversationValuesCreator;
import catchla.yep.model.IdResponse;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/9/12.
 */
public abstract class SendMessageTask<H> extends TaskRunnable<NewMessage, TaskResponse<Message>, H> implements Constants {

    private final Context context;
    private final Account account;
    private final User accountUser;

    public SendMessageTask(Context context, Account account) {
        this.context = context;
        this.account = account;
        this.accountUser = Utils.getAccountUser(context, account);
    }

    public Context getContext() {
        return context;
    }

    public Account getAccount() {
        return account;
    }

    @Nullable
    protected IdResponse uploadAttachment(final YepAPI yep, final NewMessage newMessage) throws YepException {
        return null;
    }

    @Override
    public final TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
        final YepAPI yep = YepAPIFactory.getInstance(context, account);
        long draftId = -1;
        try {
            newMessage.mediaType(getMediaType());
            draftId = saveUnsentMessage(newMessage);
            final IdResponse attachment = uploadAttachment(yep, newMessage);
            if (attachment != null) {
                newMessage.attachmentId(attachment.getId());
            }
            final Message message = yep.createMessage(newMessage.recipientType(),
                    newMessage.recipientId(), newMessage);
            updateSentMessage(draftId, message);
            return TaskResponse.getInstance(message);
        } catch (YepException e) {
            Log.w(LOGTAG, e);
            if (draftId != -1) {
                final ContentResolver cr = context.getContentResolver();
                final ContentValues values = new ContentValues();
                values.put(Messages.STATE, Messages.MessageState.FAILED);
                cr.update(Messages.CONTENT_URI, values, Expression.equals(Messages._ID, draftId).getSQL(), null);
            }
            return TaskResponse.getInstance(e);
        } catch (Throwable t) {
            Log.wtf(LOGTAG, t);
            throw new RuntimeException(t);
        }
    }

    @NonNull
    protected abstract String getMediaType();

    private void updateSentMessage(final long draftId, final Message message) {
        final ContentResolver cr = context.getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(Messages.ATTACHMENTS, JsonSerializer.serialize(message.getAttachments(),
                Attachment.class));
        values.put(Messages.STATE, Messages.MessageState.UNREAD);
        values.put(Messages.MESSAGE_ID, message.getId());
        values.put(Messages.CREATED_AT, message.getCreatedAt().getTime());
        values.put(Messages.LATITUDE, message.getLatitude());
        values.put(Messages.LONGITUDE, message.getLongitude());
        cr.update(Messages.CONTENT_URI, values, Expression.equals(Messages._ID, draftId).getSQL(), null);
    }

    private long saveUnsentMessage(final NewMessage newMessage) {
        final ContentResolver cr = context.getContentResolver();
        newMessage.localMetadata(getLocalMetadata(newMessage));
        final ContentValues values = newMessage.toDraftValues();
        //TODO update conversation entry
        final Uri inserted = cr.insert(Messages.CONTENT_URI, values);
        final Cursor cursor = cr.query(Conversations.CONTENT_URI, Conversations.COLUMNS,
                Expression.equalsArgs(Conversations.CONVERSATION_ID).getSQL(),
                new String[]{newMessage.conversationId()}, null);
        final String accountId = accountUser.getId();
        assert cursor != null;
        if (cursor.moveToFirst()) {
            // Conversation entry already exists, so just update latest info
            final ContentValues entryValues = new ContentValues();
            entryValues.put(Conversations.MEDIA_TYPE, newMessage.mediaType());
            entryValues.put(Conversations.UPDATED_AT, newMessage.createdAt());
            entryValues.put(Conversations.TEXT_CONTENT, newMessage.textContent());
            cr.update(Conversations.CONTENT_URI, entryValues, Expression.and(Expression.equalsArgs(Conversations.ACCOUNT_ID),
                            Expression.equalsArgs(Conversations.CONVERSATION_ID)).getSQL(),
                    new String[]{accountId, newMessage.conversationId()});
        } else {
            // Insert new conversation entry
            final Conversation conversation = newMessage.toConversation();
            cr.insert(Conversations.CONTENT_URI, ConversationValuesCreator.create(conversation));
        }
        cursor.close();
        assert inserted != null;
        return ParseUtils.parseLong(inserted.getLastPathSegment(), -1);
    }

    @Nullable
    protected abstract Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage);

}
