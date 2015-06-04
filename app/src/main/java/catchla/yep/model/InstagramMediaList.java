package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.util.InstagramMediaListConverter;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class InstagramMediaList extends RealmObject {

    @PrimaryKey
    private String yepUserId;
    @JsonField(name = "media", typeConverter = InstagramMediaListConverter.class)
    private RealmList<InstagramMedia> media;

    public String getYepUserId() {
        return yepUserId;
    }

    public void setYepUserId(final String yepUserId) {
        this.yepUserId = yepUserId;
    }

    public RealmList<InstagramMedia> getMedia() {
        return media;
    }

    public void setMedia(final RealmList<InstagramMedia> media) {
        this.media = media;
    }
}
