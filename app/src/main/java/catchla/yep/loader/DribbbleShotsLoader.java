package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import catchla.yep.model.DribbbleShots;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotsLoader extends AsyncTaskLoader<TaskResponse<DribbbleShots>> {

    private final Account mAccount;
    private final String mYepUserId;
    private final boolean mReadCache, mWriteCache;
    private final Realm mRealm;

    public DribbbleShotsLoader(Context context, Account account, String yepUserId, boolean readCache, boolean writeCache) {
        super(context);
        mAccount = account;
        mYepUserId = yepUserId;
        mReadCache = readCache;
        mWriteCache = writeCache;

        mRealm = Utils.getRealmForAccount(getContext(), mAccount);
    }


    @Override
    public TaskResponse<DribbbleShots> loadInBackground() {
        final YepAPI yep = YepAPIFactory.getInstance(getContext(), mAccount);
        try {
            final DribbbleShots dribbbleShots = yep.getDribbbleShots(mYepUserId);
            dribbbleShots.setYepUserId(mYepUserId);
            if (mWriteCache) {
                mRealm.copyToRealmOrUpdate(dribbbleShots);
            }
            return TaskResponse.getInstance(dribbbleShots);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
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


    @Override
    protected void onStartLoading() {
        if (isAbandoned()) return;
        if (mReadCache) {
            final RealmQuery<DribbbleShots> query = mRealm.where(DribbbleShots.class);
            query.equalTo("yepUserId", mYepUserId);
            final DribbbleShots shotsCache = query.findFirst();
            if (shotsCache != null) {
                deliverResult(TaskResponse.getInstance(shotsCache));
                return;
            }
        }
        forceLoad();
    }
}
