package catchla.yep.model;

import org.mariotaku.restfu.http.SimpleValueMap;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverQuery extends SimpleValueMap {

    public DiscoverQuery page(int page) {
        put("page", page);
        return this;
    }


    public DiscoverQuery perPage(int perPage) {
        put("per_page", perPage);
        return this;
    }

    public DiscoverQuery masterSkills(final String[] strings) {
        put("master_skills", strings);
        return this;
    }

    public DiscoverQuery learningSkills(final String[] strings) {
        put("learning_skills", strings);
        return this;
    }
}
