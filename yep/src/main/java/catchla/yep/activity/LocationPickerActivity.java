package catchla.yep.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import catchla.yep.Constants;
import catchla.yep.R;

/**
 * Created by mariotaku on 16/1/3.
 */
public class LocationPickerActivity extends ContentActivity implements Constants, LocationListener,
        LoaderManager.LoaderCallbacks<PoiResult> {

    private static final int REQUEST_LOCATION_PERMISSION = 101;
    public static final String BOUNDS = "bounds";
    public static final String POSITION = "position";
    // Views
    private SlidingUpPanelLayout mSlidingLayout;
    private MapView mMapView;

    // Listeners
    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(final View v, final int left, final int top, final int right,
                                   final int bottom, final int oldLeft, final int oldTop,
                                   final int oldRight, final int oldBottom) {
            updatePanelHeight();
        }
    };
    private LocationManager mLocationManager;
    private LocationSource.OnLocationChangedListener mOnLocationChangedListener;
    private LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(final OnLocationChangedListener listener) {
            mOnLocationChangedListener = listener;
        }

        @Override
        public void deactivate() {
            mOnLocationChangedListener = null;
        }
    };
    private AMap mMap;
    private boolean mLoaderInitialized;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_location_picker);
        mMapView.onCreate(savedInstanceState);
        setupMap();
        mSlidingLayout.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    private void setupMap() {
        mMap = mMapView.getMap();
        MyLocationStyle style = new MyLocationStyle();
        style.radiusFillColor(0x200079ff);
        style.strokeColor(Color.TRANSPARENT);
        style.strokeWidth(0);
        final Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_map_marker);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        style.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mMap.setMyLocationStyle(style);
        mMap.setLocationSource(mLocationSource);
        mMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(final CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(final CameraPosition position) {
                searchNearbyPoi(position);
            }
        });
        final UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);

        final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
    }

    private void searchNearbyPoi(final CameraPosition position) {
        final LoaderManager lm = getSupportLoaderManager();
        final Bundle args = new Bundle();
        args.putParcelable(POSITION, position.target);
        args.putParcelable(BOUNDS, mMap.getProjection().getVisibleRegion().latLngBounds);
        if (mLoaderInitialized) {
            lm.restartLoader(0, args, this);
        } else {
            lm.initLoader(0, args, this);
            mLoaderInitialized = true;
        }
    }

    private void updatePanelHeight() {
        mSlidingLayout.setPanelHeight(mSlidingLayout.getHeight() - mMapView.getHeight());
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSlidingLayout.removeOnLayoutChangeListener(mOnLayoutChangeListener);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    final Criteria criteria = new Criteria();
                    mLocationManager.requestLocationUpdates(5000L, 0f, criteria, this,
                            Looper.getMainLooper());
                    mMap.setMyLocationEnabled(true);
                } else {
                    mMap.setMyLocationEnabled(false);
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }
        super.onPause();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMapView = (MapView) findViewById(R.id.map_view);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }


    @Override
    public void onLocationChanged(final Location location) {
        if (mOnLocationChangedListener != null) {
            mOnLocationChangedListener.onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    @Override
    public void onProviderEnabled(final String provider) {

    }

    @Override
    public void onProviderDisabled(final String provider) {

    }

    @Override
    public Loader<PoiResult> onCreateLoader(final int id, final Bundle args) {
        final LatLng position = args.getParcelable(POSITION);
        final LatLngBounds region = args.getParcelable(BOUNDS);
        return new NearByPoiLoader(this, position, region);
    }

    @Override
    public void onLoadFinished(final Loader<PoiResult> loader, final PoiResult data) {
        System.identityHashCode(data);
    }

    @Override
    public void onLoaderReset(final Loader<PoiResult> loader) {

    }

    public static class NearByPoiLoader extends AsyncTaskLoader<PoiResult> {

        private final LatLng mLatLng;
        private final LatLngBounds mBounds;

        public NearByPoiLoader(final Context context, LatLng latLng, LatLngBounds bounds) {
            super(context);
            mLatLng = latLng;
            mBounds = bounds;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public PoiResult loadInBackground() {
            try {
                GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());
                RegeocodeAddress address = geocodeSearch.getFromLocation(new RegeocodeQuery(
                        new LatLonPoint(mLatLng.latitude, mLatLng.longitude), 0, GeocodeSearch.GPS));
                PoiSearch poiSearch = new PoiSearch(getContext(), new PoiSearch.Query("", "所有POI",
                        address.getAdCode()));
                poiSearch.setBound(toSearchBound(mBounds));
                return poiSearch.searchPOI();
            } catch (AMapException e) {
                Log.w(LOGTAG, e);
                return null;
            }
        }

    }

    /**
     * Fuck AMap
     *
     * @param bounds
     * @return
     */
    private static PoiSearch.SearchBound toSearchBound(final LatLngBounds bounds) {
        // Southwest
        final LatLonPoint lowerLeft = toLatLonPoint(bounds.southwest);
        // Northeast
        final LatLonPoint upperRight = toLatLonPoint(bounds.northeast);
        return new PoiSearch.SearchBound(lowerLeft, upperRight);
    }

    private static LatLonPoint toLatLonPoint(final LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }
}
