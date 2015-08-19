package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import catchla.yep.model.util.DribbbleShotListConverter;

/**
 * Created by mariotaku on 15/6/3.
 */
@JsonObject
public class DribbbleShots {

    @JsonField(name = "shots", typeConverter = DribbbleShotListConverter.class)
    private List<DribbbleShot> shots;
    private String yepUserId;

    public List<DribbbleShot> getShots() {
        return shots;
    }

    public void setShots(final List<DribbbleShot> shots) {
        this.shots = shots;
    }

    public String getYepUserId() {
        return yepUserId;
    }

    public void setYepUserId(final String yepUserId) {
        this.yepUserId = yepUserId;
    }
}
