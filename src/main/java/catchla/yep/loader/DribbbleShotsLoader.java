package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import catchla.yep.model.DribbbleShots;
import catchla.yep.util.YepAPI;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotsLoader extends CachedYepObjectLoader<DribbbleShots> {

    private final String mYepUserId;

    public DribbbleShotsLoader(Context context, Account account, String yepUserId, boolean readCache, boolean writeCache) {
        super(context, account, DribbbleShots.class, readCache, writeCache);
        mYepUserId = yepUserId;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "cached_dribbble_shots_" + getAccount().name;
    }

    @Override
    protected DribbbleShots requestData(final YepAPI yep, DribbbleShots oldData) throws YepException {
        return yep.getDribbbleShots(mYepUserId);
    }

}
