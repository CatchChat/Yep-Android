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

}
