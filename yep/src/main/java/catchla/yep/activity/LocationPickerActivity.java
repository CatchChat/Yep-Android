package catchla.yep.activity;

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
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import catchla.yep.Constants;
import catchla.yep.R;

/**
 * Created by mariotaku on 16/1/3.
 */
public class LocationPickerActivity extends ContentActivity implements Constants, LocationListener {

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
        setupMap();
        mSlidingLayout.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    private void setupMap() {
        final AMap map = mMapView.getMap();
        map.setMyLocationEnabled(true);
        map.setLocationSource(mLocationSource);
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
        map.setMyLocationStyle(style);
        final UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScaleControlsEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
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
