package catchla.yep.model;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;

import catchla.yep.util.collection.EmptyIterator;

/**
 * Created by mariotaku on 15/5/27.
 */
@JsonObject
public class PagedFriendships extends AbstractCollection<Friendship> {

    @JsonField(name = "friendships")
    List<Friendship> friendships;
    @JsonField(name = "current_page")
    int currentPage;
    @JsonField(name = "per_page")
    int perPage;
    @JsonField(name = "count")
    int count;

    @NonNull
    @Override
    public Iterator<Friendship> iterator() {
        if (friendships == null) return new EmptyIterator<>();
        return friendships.iterator();
    }

    @Override
    public int size() {
        if (friendships == null) return 0;
        return friendships.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getCount() {
        return count;
    }

    public Friendship get(final int position) {
        return friendships.get(position);
    }
}
