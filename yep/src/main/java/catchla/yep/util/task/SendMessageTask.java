package catchla.yep.util.task;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.desmond.asyncmanager.TaskRunnable;

import org.mariotaku.sqliteqb.library.Expression;

import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.TaskResponse;
import catchla.yep.provider.YepDataStore;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/9/12.
 */
public abstract class SendMessageTask<H> extends TaskRunnable<NewMessage, TaskResponse<Message>, H> {

    private final Context context;
    private final Account account;

    public SendMessageTask(Context context, Account account) {
        this.context = context;
        this.account = account;
    }

    public Context getContext() {
        return context;
    }

    public Account getAccount() {
        return account;
    }

    @Nullable
    protected NewMessage.Attachment uploadAttachment(final YepAPI yep, final NewMessage newMessage) throws YepException {
        return null;
    }

    @Override
    public final TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
        final YepAPI yep = YepAPIFactory.getInstance(context, account);
        try {
            newMessage.mediaType(getMediaType());
            final long draftId = saveUnsentMessage(newMessage);
            newMessage.attachment(uploadAttachment(yep, newMessage));
            final NewMessage.JsonBody messageBody = newMessage.toJson();
            final Message message = yep.createMessage(messageBody);
            updateSentMessage(draftId, message);
            return TaskResponse.getInstance(message);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    @NonNull
    protected abstract String getMediaType();

    private void updateSentMessage(final long draftId, final Message message) {
        final ContentResolver cr = context.getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(Messages.ATTACHMENTS, JsonSerializer.serialize(message.getAttachments(),
                Message.Attachment.class));
        values.put(Messages.STATE, Messages.MessageState.UNREAD);
        values.put(Messages.MESSAGE_ID, message.getId());
        values.put(Messages.CREATED_AT, message.getCreatedAt().getTime());
        cr.update(Messages.CONTENT_URI, values, Expression.equals(Messages._ID, draftId).getSQL(), null);
    }

    private long saveUnsentMessage(final NewMessage newMessage) {
        final ContentResolver cr = context.getContentResolver();
        newMessage.localMetadata(getLocalMetadata(newMessage));
        final ContentValues values = newMessage.toDraftValues();
        //TODO update conversation entry
        final Uri inserted = cr.insert(Messages.CONTENT_URI, values);
        return ParseUtils.parseLong(inserted.getLastPathSegment(), -1);
    }

    @Nullable
    protected abstract Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage);

}
