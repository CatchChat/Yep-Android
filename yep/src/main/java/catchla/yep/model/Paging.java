package catchla.yep.model;

import android.support.v4.util.ArrayMap;

/**
 * Created by mariotaku on 15/5/28.
 */
public class Paging extends ArrayMap<String, String> {

    public Paging perPage(int perPage) {
        put("per_page", String.valueOf(perPage));
        return this;
    }

    public Paging page(int page) {
        put("page", String.valueOf(page));
        return this;
    }

    public Paging minId(String minId) {
        put("min_id", minId);
        return this;
    }

    public Paging maxId(String maxId) {
        put("max_id", maxId);
        return this;
    }

}
