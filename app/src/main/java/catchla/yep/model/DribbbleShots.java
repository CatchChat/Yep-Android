package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.util.DribbbleShotListTypeConverter;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mariotaku on 15/6/3.
 */
@JsonObject
public class DribbbleShots extends RealmObject {

    @JsonField(name = "shots", typeConverter = DribbbleShotListTypeConverter.class)
    private RealmList<DribbbleShot> shots;
    @PrimaryKey
    private String yepUserId;

    public RealmList<DribbbleShot> getShots() {
        return shots;
    }

    public void setShots(final RealmList<DribbbleShot> shots) {
        this.shots = shots;
    }

    public String getYepUserId() {
        return yepUserId;
    }

    public void setYepUserId(final String yepUserId) {
        this.yepUserId = yepUserId;
    }
}
