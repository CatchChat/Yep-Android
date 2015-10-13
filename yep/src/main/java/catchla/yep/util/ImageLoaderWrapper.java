package catchla.yep.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import catchla.yep.R;

/**
 * Created by mariotaku on 15/10/13.
 */
public class ImageLoaderWrapper {
    private final ImageLoader imageLoader;
    private DisplayImageOptions profileImageOption;

    public ImageLoaderWrapper(final ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        DisplayImageOptions.Builder piBuilder = new DisplayImageOptions.Builder();
        piBuilder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        piBuilder.showImageOnLoading(R.drawable.ic_profile_image_default);
        profileImageOption = piBuilder.build();
    }

    public void displayProfileImage(final String uri, final ImageView view) {
        imageLoader.displayImage(uri, view, profileImageOption);
    }

    public void cancelDisplayTask(final ImageView imageView) {
        imageLoader.cancelDisplayTask(imageView);
    }

    public void displaySkillBannerImage(final String uri, final ImageView view) {
        imageLoader.displayImage(uri, view);
    }

    public void displayImage(final String uri, final ImageView view) {
        imageLoader.displayImage(uri, view);
    }

    public void displayImage(final String uri, final ImageView view,
                             final ImageLoadingListener loadingListener,
                             final ImageLoadingProgressListener progressListener) {
        imageLoader.displayImage(uri, view, null, loadingListener, progressListener);
    }
}
