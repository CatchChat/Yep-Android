package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/8/7.
 */
@JsonObject
public class LatLng {
    @JsonField(name = "latitude")
    double latitude;
    @JsonField(name = "longitude")
    double longitude;

    public LatLng() {
    }

    public LatLng(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {

        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
