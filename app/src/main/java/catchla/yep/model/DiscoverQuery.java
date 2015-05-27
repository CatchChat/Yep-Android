package catchla.yep.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import catchla.yep.util.net.JSONTypedData;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverQuery extends JSONTypedData {
    DiscoverQuery(final JSONObject json) {
        super(json);
    }

    public static class Builder {

        private final JSONObject jsonObject = new JSONObject();

        public Builder masterSkills(String[] masterSkills) {
            final JSONArray array = new JSONArray();
            for (String skill : masterSkills) {
                array.put(skill);
            }
            try {
                jsonObject.put("master_skills", array);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder learningSkills(String[] learningSkills) {
            final JSONArray array = new JSONArray();
            for (String skill : learningSkills) {
                array.put(skill);
            }
            try {
                jsonObject.put("learning_skills", array);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder page(int page) {
            try {
                jsonObject.put("page", page);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder prePage(int pre_page) {
            try {
                jsonObject.put("pre_page", pre_page);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public DiscoverQuery build() {
            return new DiscoverQuery(jsonObject);
        }
    }
}
