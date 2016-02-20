package catchla.yep.util;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiSearch;

/**
 * Fuck AMap
 * Created by mariotaku on 16/1/6.
 */
public class AMapModelUtils {

    public static PoiSearch.SearchBound toSearchBound(final LatLngBounds bounds) {
        // Southwest
        final LatLonPoint lowerLeft = toLatLonPoint(bounds.southwest);
        // Northeast
        final LatLonPoint upperRight = toLatLonPoint(bounds.northeast);
        return new PoiSearch.SearchBound(lowerLeft, upperRight);
    }

    private static LatLonPoint toLatLonPoint(final LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    public static LatLng toLatLng(final LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }
}
