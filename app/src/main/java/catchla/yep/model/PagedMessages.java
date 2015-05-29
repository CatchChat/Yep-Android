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
public class PagedMessages extends AbstractCollection<Message> {

    @JsonField(name = "messages")
    List<Message> messages;
    @JsonField(name = "current_page")
    int currentPage;
    @JsonField(name = "per_page")
    int perPage;
    @JsonField(name = "count")
    int count;

    @NonNull
    @Override
    public Iterator<Message> iterator() {
        if (messages == null) return new EmptyIterator<>();
        return messages.iterator();
    }

    @Override
    public int size() {
        if (messages == null) return 0;
        return messages.size();
    }

    public List<Message> getMessages() {
        return messages;
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

    public Message get(final int position) {
        return messages.get(position);
    }
}
