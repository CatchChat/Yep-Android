package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import catchla.yep.model.GithubUserInfo;
import catchla.yep.util.YepAPI;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/6/3.
 */
public class GithubUserInfoLoader extends CachedYepObjectLoader<GithubUserInfo> {

    private final String mYepUserId;

    public GithubUserInfoLoader(Context context, Account account, String yepUserId, boolean readCache, boolean writeCache) {
        super(context, account, GithubUserInfo.class, readCache, writeCache);
        mYepUserId = yepUserId;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "cached_github_user_info_" + getAccount().name;
    }

    @Override
    protected GithubUserInfo requestData(final YepAPI yep, GithubUserInfo oldData) throws YepException {
        return yep.getGithubUserInfo(mYepUserId);
    }

}
