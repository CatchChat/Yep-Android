package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import catchla.yep.model.InstagramMediaList;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/6/3.
 */
public class InstagramMediaLoader extends CachedYepObjectLoader<InstagramMediaList> {

    private final String mYepUserId;

    public InstagramMediaLoader(Context context, Account account, String yepUserId, boolean readCache, boolean writeCache) {
        super(context, account, InstagramMediaList.class, readCache, writeCache);
        mYepUserId = yepUserId;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "cached_instagram_media_" + getAccount().name;
    }

    @Override
    protected InstagramMediaList requestData(final YepAPI yep, InstagramMediaList oldData) throws YepException {
        return yep.getInstagramMediaList(mYepUserId);
    }

}
