package catchla.yep.model;

import org.mariotaku.restfu.http.SimpleValueMap;

/**
 * Created by mariotaku on 15/5/28.
 */
public class Paging extends SimpleValueMap {

    public Paging perPage(int perPage) {
        put("per_page", perPage);
        return this;
    }

    public Paging page(int page) {
        put("page", page);
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
