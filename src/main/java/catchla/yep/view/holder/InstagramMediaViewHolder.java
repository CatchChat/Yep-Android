package catchla.yep.view.holder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.InstagramMediaAdapter;
import catchla.yep.model.InstagramImage;
import catchla.yep.model.InstagramMedia;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/6/3.
 */
public class InstagramMediaViewHolder extends RecyclerView.ViewHolder implements ImageLoadingListener {
    private final ImageView imageView;
    private final View imageProgress;
    private final InstagramMediaAdapter adapter;

    public InstagramMediaViewHolder(final View itemView, final InstagramMediaAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        imageProgress = itemView.findViewById(R.id.image_progress);
    }

    public void displayShot(InstagramMedia shot) {
        final InstagramImage image = getBestImage(shot.getImages());
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        if (image != null) {
            imageLoader.displayImage(image.getUrl(), imageView, this, null);
        } else {
            imageLoader.cancelDisplayTask(imageView);
        }
    }

    private InstagramImage getBestImage(final List<InstagramImage> images) {
        if (images == null) return null;
        for (InstagramImage image : images) {
            if ("low_resolution".equals(image.getResolution())) return image;
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
