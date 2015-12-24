package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import catchla.yep.model.iface.JsonBody;

/**
 * Created by mariotaku on 15/10/12.
 */
@JsonObject
public class NewTopic implements JsonBody {

    @JsonField(name = "body")
    String body;
    @JsonField(name = "skill_id")
    String skillId;
    @JsonField(name = "attachments")
    List<IdResponse> attachments;
    @JsonField(name = "latitude")
    double latitude;
    @JsonField(name = "longitude")
    double longitude;

    public NewTopic body(String body) {
        this.body = body;
        return this;
    }

    public NewTopic skillId(String skillId) {
        this.skillId = skillId;
        return this;
    }

    public void attachments(final List<IdResponse> attachments) {
        if (attachments == null) return;
        this.attachments = attachments;
    }

    public NewTopic location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }


}
