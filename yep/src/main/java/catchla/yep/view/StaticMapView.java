package catchla.yep.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import javax.inject.Inject;

import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.StaticMapUrlGenerator;
import catchla.yep.util.dagger.ApplicationModule;
import catchla.yep.util.dagger.DaggerGeneralComponent;
import catchla.yep.util.dagger.GeneralComponentHelper;

/**
 * Created by mariotaku on 15/12/9.
 */
public class StaticMapView extends ImageView {
    StaticMapUrlGenerator mGenerator = new StaticMapUrlGenerator();
    @Inject
    ImageLoaderWrapper mImageLoader;
    private Location mLocation;
    private int mZoomLevel;
    private boolean mScaleToDensity;

    public StaticMapView(final Context context) {
        super(context);
        init(context);
    }

    public StaticMapView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StaticMapView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StaticMapView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        GeneralComponentHelper.build(context).inject(this);
        setScaleType(ScaleType.CENTER_CROP);
    }

    public void setProvider(final StaticMapUrlGenerator.Provider provider) {
        mGenerator.setProvider(provider);
    }

    public void setScaleToDensity(boolean scaleToDensity) {
        mGenerator.setScale(scaleToDensity ? getResources().getDisplayMetrics().density : 1);
        loadImage();
    }

    public void display(Location location, int zoomLevel) {
        mLocation = location;
        mZoomLevel = zoomLevel;
        if (!mGenerator.hasSize()) {
            mGenerator.setSize(getWidth(), getHeight());
        }
        loadImage();
    }

    private void loadImage() {
        if (mLocation == null || !mGenerator.hasSize()) return;
        mImageLoader.displayImage(mGenerator.generate(mLocation, mZoomLevel), this);
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            mGenerator.setSize(w, h);
            loadImage();
        }
    }
}
