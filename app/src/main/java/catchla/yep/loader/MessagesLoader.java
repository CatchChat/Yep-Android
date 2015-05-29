package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by mariotaku on 15/5/29.
 */
public class MessagesLoader extends RealmLoader<Message> {
    private final Conversation mConversation;

    public MessagesLoader(final Context context, final Account account, final Conversation conversation) {
        super(context, account);
        mConversation = conversation;
    }

    @Override
    protected void onStartLoading() {
        Realm realm = getRealm();
        final RealmQuery<Message> where = realm.where(Message.class);
        where.equalTo("conversationId", mConversation.getId());
        deliverResult(where.findAll());
    }

}
