package catchla.yep.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps2d.MapView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import catchla.yep.R;
import catchla.yep.view.iface.IExtendedView;

/**
 * Created by mariotaku on 16/1/3.
 */
public class LocationPickerActivity extends ContentActivity {

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        mMapView.onCreate(savedInstanceState);
        getMainContent().setOnSizeChangedListener(new IExtendedView.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(final View view, final int w, final int h, final int oldw, final int oldh) {
                updatePanelHeight();
            }
        });
        getMainContent().setOnFitSystemWindowsListener(new IExtendedView.OnFitSystemWindowsListener() {
            @Override
            public void onFitSystemWindows(final Rect insets) {
                updatePanelHeight();
            }
        });
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
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMapView = (MapView) findViewById(R.id.map_view);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }
}
