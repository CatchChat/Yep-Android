package catchla.yep.model;

import com.bluelinelabs.logansquare.LoganSquare;

import org.mariotaku.restfu.http.SimpleValueMap;

import java.io.IOException;

/**
 * Created by mariotaku on 15/10/12.
 */
public class NewTopic extends SimpleValueMap {

    public JsonBody toJson() {
        try {
            final String json = LoganSquare.serialize(asMap());
            return new JsonBody(json);
        } catch (IOException e) {
            return null;
        }
    }

    public NewTopic body(String body) {
        put("body", body);
        return this;
    }

    public NewTopic skillId(String skillId) {
        put("skill_id", skillId);
        return this;
    }

    public <T extends NewAttachment> void attachment(final T attachment) {
        if (attachment == null) return;
        //noinspection unchecked
        put("attachments", attachment);
    }

    public NewTopic location(double latitude, double longitude) {
        put("latitude", latitude);
        put("longitude", longitude);
        return this;
    }


    public static final class JsonBody extends AbsJsonBody {

        private JsonBody(String json) {
            super(json);
        }

    }
}
