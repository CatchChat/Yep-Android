package catchla.yep.model;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import catchla.yep.util.collection.EmptyIterator;

/**
 * Created by mariotaku on 15/5/27.
 */
@JsonObject
public class PagedFriendships extends AbstractCollection<Friendship> implements Iterable<Friendship> {

    @JsonField(name = "friendships")
    ArrayList<Friendship> friendships;
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

    public Friendship get(final int position) {
        return friendships.get(position);
    }
}
