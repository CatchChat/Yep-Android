package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mariotaku on 15/5/27.
 */
@JsonObject
public class PagedUsers implements Iterable<User> {

    @JsonField(name = "users")
    ArrayList<User> users;
    @JsonField(name = "current_page")
    int currentPage;
    @JsonField(name = "per_page")
    int perPage;
    @JsonField(name = "count")
    int count;

    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }

    public int size() {
        return users.size();
    }

    public User get(final int position) {
        return users.get(position);
    }
}
