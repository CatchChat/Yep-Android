package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import catchla.yep.R;
import catchla.yep.model.InstagramImage;
import catchla.yep.model.InstagramMedia;

/**
 * Created by mariotaku on 15/6/3.
 */
public class InstagramMediaViewHolder extends RecyclerView.ViewHolder implements RequestListener<String, GlideDrawable> {
    private final ImageView imageView;
    private final View imageProgress;

    public InstagramMediaViewHolder(final View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        imageProgress = itemView.findViewById(R.id.image_progress);
    }

    public void displayShot(InstagramMedia shot) {
        final InstagramImage image = getBestImage(shot.getImages());
        if (image != null) {
            imageProgress.setVisibility(View.VISIBLE);
            Glide.with(itemView.getContext()).load(image.getUrl()).listener(this).into(imageView);
        } else {
            imageProgress.setVisibility(View.GONE);
            Glide.clear(imageView);
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
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResourc) {
        imageProgress.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(final GlideDrawable resource, final String model, final Target<GlideDrawable> target,
                                   final boolean isFromMemoryCache, final boolean isFirstResource) {
        imageProgress.setVisibility(View.GONE);
        return false;
    }
}
