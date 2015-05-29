package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;

import catchla.yep.model.Conversation;
import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by mariotaku on 14-8-5.
 */
public class ConversationsLoader extends RealmLoader<Conversation> {

    private final RealmChangeListener mChangeListener;

    public ConversationsLoader(Context context, Account account) {
        super(context, account);
        mChangeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                if (isAbandoned()) return;
                startLoading();
            }
        };
    }

    @Override
    protected void onStartLoading() {
        Realm realm = getRealm();
//        realm.removeChangeListener(mChangeListener);
//        realm.addChangeListener(mChangeListener);
        deliverResult(realm.where(Conversation.class).findAll());
    }


}
