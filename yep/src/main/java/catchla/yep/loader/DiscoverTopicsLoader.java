package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

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

    @Topic.SortOrder
    private final String mSortBy;

    public DiscoverTopicsLoader(Context context, Account account, @Topic.SortOrder String sortBy,
                                boolean readCache, boolean writeCache) {
        super(context, account, Topic.class, readCache, writeCache);
        mSortBy = sortBy;
    }

    @NonNull
    @Override
    protected String getCacheFileName() {
        return "discover_topics_cache_" + getAccount().name;
    }

    @Override
    protected List<Topic> requestData(final YepAPI yep) throws YepException {
        final Paging paging = new Paging();
        int page = 1;
        final List<Topic> list = new ArrayList<>();
        PagedTopics topics;
        while ((topics = yep.getDiscoverTopics(mSortBy, paging)).size() > 0) {
            list.addAll(topics);
            paging.page(++page);
            if (topics.getCount() < topics.getPerPage()) break;
        }
        return list;
    }


}
