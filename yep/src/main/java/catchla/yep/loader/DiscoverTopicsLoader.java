package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.PagedTopics;
import catchla.yep.model.Paging;
import catchla.yep.model.Topic;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverTopicsLoader extends CachedYepListLoader<Topic> implements Constants {

    private final String mUserId;
    @Topic.SortOrder
    private final String mSortBy;
    private final Paging mPaging;

    public DiscoverTopicsLoader(@NonNull Context context, @NonNull Account account,
                                @Nullable final String userId, @Nullable Paging paging,
                                @Topic.SortOrder String sortBy, boolean readCache, boolean writeCache,
                                @Nullable List<Topic> oldData) {
        super(context, account, Topic.class, oldData, readCache, writeCache);
        mUserId = userId;
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
        final PagedTopics topics;
        if (mUserId != null) {
            topics = yep.getTopics(mUserId, mPaging);
        } else {
            topics = yep.getDiscoverTopics(mSortBy, mPaging);
        }
        for (Topic topic : topics) {
            list.remove(topic);
            list.add(topic);
        }
        return list;
    }


}
