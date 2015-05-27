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

    int currentPage;
    int perPage;
    int count;

    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }
}
