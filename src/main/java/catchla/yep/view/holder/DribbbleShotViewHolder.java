package catchla.yep.view.holder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.DribbbleShotsAdapter;
import catchla.yep.model.DribbbleShot;
import catchla.yep.model.DribbbleShotImage;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotViewHolder extends RecyclerView.ViewHolder implements ImageLoadingListener {

    private final DribbbleShotsAdapter adapter;

    private final ImageView imageView;
    private final View imageProgress;

    public DribbbleShotViewHolder(final DribbbleShotsAdapter adapter, final View itemView) {
        super(itemView);
        this.adapter = adapter;
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        imageProgress = itemView.findViewById(R.id.image_progress);
    }

    public void displayShot(DribbbleShot shot) {
        final DribbbleShotImage image = getBestImage(shot.getImages());
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        if (image != null) {
            imageLoader.displayImage(image.getUrl(), imageView, this, null);
        } else {
            imageLoader.cancelDisplayTask(imageView);
        }
    }

    private DribbbleShotImage getBestImage(final List<DribbbleShotImage> images) {
        if (images == null) return null;
        for (DribbbleShotImage image : images) {
            if ("normal".equals(image.getResolution())) return image;
        }
        return null;
    }

    @Override
    public void onLoadingStarted(final String imageUri, final View view) {
        imageProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
        imageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
        imageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view) {
        imageProgress.setVisibility(View.GONE);
    }
}
