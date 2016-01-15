package catchla.yep.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hannesdorfmann.adapterdelegates.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates.ListDelegationAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.util.AMapModelUtils;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 16/1/3.
 */
public class LocationPickerActivity extends ContentActivity implements Constants, LocationListener,
        LoaderManager.LoaderCallbacks<PoiResult> {

    private static final int REQUEST_LOCATION_PERMISSION = 101;
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
    private RecyclerView mPlacesList;
    private LocationAdapter mAdapter;
    private Marker mMarker;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_picker, menu);
        return true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setContentView(R.layout.activity_location_picker);
        mMapView.onCreate(savedInstanceState);
        mAdapter = new LocationAdapter(this);
        mPlacesList.setAdapter(mAdapter);
        mPlacesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSlidingLayout.addOnLayoutChangeListener(mOnLayoutChangeListener);
        mSlidingLayout.setScrollableView(mPlacesList);
        setupMap();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.use_location: {
                final Location myLocation = mMap.getMyLocation();
                final Intent data = new Intent();
                if (mMarker != null) {
                    final Location location = new Location("");
                    final LatLng position = mMarker.getPosition();
                    location.setLatitude(position.latitude);
                    location.setLongitude(position.longitude);
                    data.putExtra(EXTRA_LOCATION, location);
                    data.putExtra(EXTRA_NAME, mMarker.getTitle());
                } else if (myLocation != null) {
                    data.putExtra(EXTRA_LOCATION, myLocation);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                setResult(RESULT_OK, data);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMap() {
        mMap = mMapView.getMap();
        MyLocationStyle style = new MyLocationStyle();
        style.radiusFillColor(0x200079ff);
        style.strokeColor(Color.TRANSPARENT);
        style.strokeWidth(0);
        final Bitmap bitmap = Utils.getMarkerBitmap(this);
        style.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        style.anchor(.5f, .5f);
        mMap.setMyLocationStyle(style);
        mMap.setLocationSource(mLocationSource);
        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                showMarker(latLng, "Pin on map", false);
            }
        });
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

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
        mPlacesList = (RecyclerView) findViewById(R.id.places_list);
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
        final ArrayList<Object> items = new ArrayList<>();
        items.add(new CurrentLocation());
        if (data != null) {
            items.addAll(data.getPois());
        }
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(final Loader<PoiResult> loader) {
        mAdapter.setItems(null);
        mAdapter.notifyDataSetChanged();
    }

    private void notifyCurrentLocationClick() {
        final Location myLocation = mMap.getMyLocation();
        if (myLocation == null) return;
        final LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        showMarker(latLng, "My location", true);
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void notifyPoiItemClick(final PoiItem item) {
        final LatLng latLng = AMapModelUtils.toLatLng(item.getLatLonPoint());
        showMarker(latLng, item.getTitle(), true);
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void showMarker(final LatLng latLng, final String name, final boolean center) {
        if (mMarker != null) {
            mMarker.remove();
            mMarker.destroy();
            mMarker = null;
        }
        if (latLng == null) return;
        final MarkerOptions options = new MarkerOptions();
        options.draggable(false);
        options.position(latLng);
        options.title(name);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_pin));
        options.anchor(0.5f, 1f);
        mMarker = mMap.addMarker(options);
        if (center) {
            mMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    public static class NearByPoiLoader extends AsyncTaskLoader<PoiResult> {

        @Nullable
        private final LatLng mLatLng;
        @Nullable
        private final LatLngBounds mBounds;

        public NearByPoiLoader(final Context context, @Nullable LatLng latLng, @Nullable LatLngBounds bounds) {
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
            if (mLatLng == null || mBounds == null) return null;
            try {
                GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());
                RegeocodeAddress address = geocodeSearch.getFromLocation(new RegeocodeQuery(
                        new LatLonPoint(mLatLng.latitude, mLatLng.longitude), 0, GeocodeSearch.GPS));
                PoiSearch poiSearch = new PoiSearch(getContext(), new PoiSearch.Query("", "景点",
                        address.getAdCode()));
                poiSearch.setBound(AMapModelUtils.toSearchBound(mBounds));
                return poiSearch.searchPOI();
            } catch (AMapException e) {
                return null;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

    }

    public static class LocationAdapter extends ListDelegationAdapter<List<Object>> {

        private final LocationPickerActivity activity;

        public LocationAdapter(LocationPickerActivity activity) {
            this.activity = activity;
            // DelegatesManager is a protected Field in ListDelegationAdapter
            delegatesManager.addDelegate(new CurrentLocationDelegate(activity, this, activity.getLayoutInflater(), 0));
            delegatesManager.addDelegate(new PoiItemDelegate(activity, this, activity.getLayoutInflater(), 1));
        }

        public void notifyItemClick(final int position) {
            Object item = items.get(position);
            if (item instanceof PoiItem) {
                activity.notifyPoiItemClick(((PoiItem) item));
            } else if (item instanceof CurrentLocation) {
                activity.notifyCurrentLocationClick();
            }
        }
    }

    private static class PoiItemDelegate implements AdapterDelegate<List<Object>> {
        private final LayoutInflater inflater;
        private final int viewType;
        private final LocationAdapter adapter;

        public PoiItemDelegate(final Context context, final LocationAdapter adapter,
                               final LayoutInflater inflater, final int viewType) {
            this.adapter = adapter;
            this.inflater = inflater;
            this.viewType = viewType;
        }

        @Override
        public int getItemViewType() {
            return viewType;
        }

        @Override
        public boolean isForViewType(@NonNull final List<Object> items, final int position) {
            return items.get(position) instanceof PoiItem;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
            return new PlaceViewHolder(adapter, inflater.inflate(R.layout.list_item_location_picker_place, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final List<Object> items, final int position, @NonNull final RecyclerView.ViewHolder holder) {
            final PoiItem poiItem = (PoiItem) items.get(position);
            ((PlaceViewHolder) holder).display(R.drawable.ic_place_pin, poiItem.getTitle());
        }


    }

    static class CurrentLocation {

    }

    private static class CurrentLocationDelegate implements AdapterDelegate<List<Object>> {
        private final Context context;
        private final LayoutInflater inflater;
        private final int viewType;
        private final LocationAdapter adapter;

        public CurrentLocationDelegate(final Context context, final LocationAdapter adapter,
                                       final LayoutInflater inflater, final int viewType) {
            this.context = context;
            this.adapter = adapter;
            this.inflater = inflater;
            this.viewType = viewType;
        }

        @Override
        public int getItemViewType() {
            return viewType;
        }

        @Override
        public boolean isForViewType(@NonNull final List<Object> items, final int position) {
            return items.get(position) instanceof CurrentLocation;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent) {
            return new PlaceViewHolder(adapter, inflater.inflate(R.layout.list_item_location_picker_place, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final List<Object> items, final int position, @NonNull final RecyclerView.ViewHolder holder) {
            ((PlaceViewHolder) holder).display(R.drawable.ic_place_current_location, context.getString(R.string.my_current_location));
        }

    }

    private static class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iconView;
        private final LocationAdapter adapter;
        private TextView titleView;

        public PlaceViewHolder(LocationAdapter adapter, final View view) {
            super(view);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            iconView = (ImageView) itemView.findViewById(android.R.id.icon);
            titleView = (TextView) itemView.findViewById(android.R.id.title);
        }

        public void display(final int icon, CharSequence title) {
            iconView.setImageResource(icon);
            titleView.setText(title);
        }

        @Override
        public void onClick(final View v) {
            adapter.notifyItemClick(getLayoutPosition());
        }
    }
}
