package catchla.yep.util.task;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.desmond.asyncmanager.TaskRunnable;

import org.mariotaku.sqliteqb.library.Expression;

import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.TaskResponse;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/9/12.
 */
public abstract class SendMessageTask extends TaskRunnable<NewMessage, TaskResponse<Message>, SendMessageTask.SendMessageHandler> {

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

    protected abstract boolean prepareNewMessage(final YepAPI yep, final NewMessage newMessage);

    @Override
    public TaskResponse<Message> doLongOperation(final NewMessage newMessage) throws InterruptedException {
        final YepAPI yep = YepAPIFactory.getInstance(context, account);
        try {
            final long draftId = saveUnsentMessage(newMessage);
            prepareNewMessage(yep, newMessage);
            final NewMessage.JsonBody messageBody = newMessage.toJson();
            final Message message = yep.createMessage(messageBody);
            updateSentMessage(draftId, message);
            return TaskResponse.getInstance(message);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    protected void updateSentMessage(final long draftId, final Message message) {
        final ContentResolver cr = context.getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(Messages.ATTACHMENTS, JsonSerializer.serialize(message.getAttachments(), Message.Attachment.class));
        cr.update(Messages.CONTENT_URI, values, Expression.equals(Messages._ID, draftId).getSQL(), null);
    }

    protected long saveUnsentMessage(final NewMessage newMessage) {
        final ContentResolver cr = context.getContentResolver();
        final ContentValues values = newMessage.toDraftValues();
        return ParseUtils.parseLong(cr.insert(Messages.CONTENT_URI, values).getLastPathSegment(), -1);
    }

    public interface SendMessageHandler {

    }
}
