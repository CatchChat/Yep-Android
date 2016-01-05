package catchla.yep.activity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import catchla.yep.R;

/**
 * Created by mariotaku on 16/1/3.
 */
public class LocationPickerActivity extends ContentActivity implements LocationListener {

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_location_picker);
        mMapView.onCreate(savedInstanceState);
        final AMap map = mMapView.getMap();
        map.setMyLocationEnabled(true);
        map.setLocationSource(mLocationSource);
        final UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScaleControlsEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        mSlidingLayout.addOnLayoutChangeListener(mOnLayoutChangeListener);
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
        final Criteria criteria = new Criteria();
        mLocationManager.requestLocationUpdates(5000L, 0f, criteria, this, Looper.getMainLooper());
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        mLocationManager.removeUpdates(this);
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
}
