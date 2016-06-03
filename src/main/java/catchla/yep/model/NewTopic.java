package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Arrays;
import java.util.List;

import catchla.yep.model.iface.JsonBody;
import catchla.yep.model.util.SerializeOnlyJsonArrayConverter;

/**
 * Created by mariotaku on 15/10/12.
 */
@JsonObject
public class NewTopic implements JsonBody {

    @JsonField(name = "kind")
    String kind;
    @JsonField(name = "body")
    String body;
    @JsonField(name = "skill_id")
    String skillId;
    @JsonField(name = "attachments", typeConverter = SerializeOnlyJsonArrayConverter.class)
    List<Object> attachments;
    @JsonField(name = "latitude")
    double latitude;
    @JsonField(name = "longitude")
    double longitude;

    public NewTopic body(String body) {
        this.body = body;
        return this;
    }

    public NewTopic kind(String kind) {
        this.kind = kind;
        return this;
    }

    public NewTopic skillId(String skillId) {
        this.skillId = skillId;
        return this;
    }

    public void attachments(Object... attachments) {
        attachments(Arrays.asList(attachments));
    }

    public void attachments(final List<?> attachments) {
        if (attachments == null) return;
        //noinspection unchecked
        this.attachments = (List<Object>) attachments;
    }

    public NewTopic location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }


}
