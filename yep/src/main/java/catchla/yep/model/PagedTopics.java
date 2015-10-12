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
public class PagedTopics extends AbstractCollection<Topic> {

    @JsonField(name = "topics")
    List<Topic> users;
    @JsonField(name = "current_page")
    int currentPage;
    @JsonField(name = "per_page")
    int perPage;
    @JsonField(name = "count")
    int count;

    @NonNull
    @Override
    public Iterator<Topic> iterator() {
        if (users == null) return new EmptyIterator<>();
        return users.iterator();
    }

    @Override
    public int size() {
        if (users == null) return 0;
        return users.size();
    }

    public Topic get(final int position) {
        return users.get(position);
    }

    public int getCount() {
        return count;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<Topic> getUsers() {
        return users;
    }
}
