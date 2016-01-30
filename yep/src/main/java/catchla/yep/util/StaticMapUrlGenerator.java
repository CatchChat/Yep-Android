package catchla.yep.util;

import android.location.Location;
import android.net.Uri;

/**
 * Created by mariotaku on 15/12/9.
 */
public class StaticMapUrlGenerator {

    private Provider provider;
    private int width, height;
    private float scale = 1;

    public void setProvider(final Provider provider) {
        this.provider = provider;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String generate(Location location, int zoomLevel) {
        return provider.generate(location, width, height, scale, zoomLevel);
    }

    public boolean hasSize() {
        return width != 0 && height != 0;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public static abstract class Provider {

        public abstract String generate(final Location location, final int width, final int height,
                                        final float scale, final int zoomLevel);
    }

    public static class AMapProvider extends Provider {

        private final String apiKey;

        public AMapProvider(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public String generate(final Location location, final int width, final int height, final float scale, final int zoomLevel) {
            final Uri.Builder builder = Uri.parse("https://restapi.amap.com/v3/staticmap").buildUpon();
            builder.appendQueryParameter("location", location.getLongitude() + "," + location.getLatitude());
            builder.appendQueryParameter("zoom", String.valueOf(zoomLevel));
            final int scaleInt = Math.min((int) Math.floor(scale), 2);
            builder.appendQueryParameter("scale", String.valueOf(scaleInt));
            builder.appendQueryParameter("size", Math.round(width / scale) + "*" + Math.round(height / scale));
            builder.appendQueryParameter("key", apiKey);
            final Uri build = builder.build();
            return build.toString();
        }
    }

    public static class OpenStreetMapProvider extends Provider {
        private final MapType mapType;

        public OpenStreetMapProvider(MapType mapType) {
            this.mapType = mapType;
        }

        @Override
        public String generate(final Location location, final int width, final int height, final float scale, final int zoomLevel) {
            final Uri.Builder builder = Uri.parse("http://staticmap.openstreetmap.de/staticmap.php").buildUpon();
            builder.appendQueryParameter("zoom", String.valueOf(zoomLevel));
            builder.appendQueryParameter("center", location.getLatitude() + "," + location.getLongitude());
            builder.appendQueryParameter("maptype", mapType.value);
            builder.appendQueryParameter("size", Math.round(width / scale) + "x" + Math.round(height / scale));
            return builder.build().toString();
        }

        public enum MapType {
            MAPNIK("mapnik");
            final String value;

            MapType(String value) {
                this.value = value;
            }
        }
    }
}
