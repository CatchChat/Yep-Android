package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.Paging;
import catchla.yep.model.Topic;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverTopicsLoader extends CachedYepListLoader<Topic> implements Constants {

    @Topic.SortOrder
    private final String mSortBy;
    private final Paging mPaging;

    public DiscoverTopicsLoader(Context context, Account account, @Topic.SortOrder String sortBy,
                                List<Topic> oldData, Paging paging, boolean readCache, boolean writeCache) {
        super(context, account, Topic.class, oldData, readCache, writeCache);
        mSortBy = sortBy;
        mPaging = paging;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "discover_topics_cache_" + getAccount().name;
    }

    @Override
    protected List<Topic> requestData(final YepAPI yep, List<Topic> oldData) throws YepException {
        final List<Topic> list = new ArrayList<>();
        if (oldData != null) {
            list.addAll(oldData);
        }
        for (Topic topic : yep.getDiscoverTopics(mSortBy, mPaging)) {
            list.remove(topic);
            list.add(topic);
        }
        return list;
    }


}
