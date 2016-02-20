package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import catchla.yep.model.util.InstagramMediaListConverter;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class InstagramMediaList {

    private String yepUserId;
    @JsonField(name = "media", typeConverter = InstagramMediaListConverter.class)
    private List<InstagramMedia> media;

    public String getYepUserId() {
        return yepUserId;
    }

    public void setYepUserId(final String yepUserId) {
        this.yepUserId = yepUserId;
    }

    public List<InstagramMedia> getMedia() {
        return media;
    }

    public void setMedia(final List<InstagramMedia> media) {
        this.media = media;
    }
}
