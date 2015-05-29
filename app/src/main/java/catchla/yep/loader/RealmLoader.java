package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.Loader;

import catchla.yep.util.Utils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by mariotaku on 14-8-5.
 */
public class RealmLoader<T extends RealmObject> extends Loader<RealmResults<T>> {

    private final Account mAccount;
    private final Realm mRealm;

    public RealmLoader(Context context, Account account) {
        super(context);
        mAccount = account;
        mRealm = Utils.getRealmForAccount(getContext(), mAccount);
    }

    public Account getAccount() {
        return mAccount;
    }

    protected Realm getRealm() {
        return mRealm;
    }

    protected void onRealmClosed() {

    }

    @Override
    protected void onReset() {
        super.onReset();
        if (isAbandoned()) {
            onRealmClosed();
            mRealm.close();
        }
    }


}
