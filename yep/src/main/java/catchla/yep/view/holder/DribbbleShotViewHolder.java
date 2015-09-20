package catchla.yep.view.holder;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import catchla.yep.R;
import catchla.yep.model.DribbbleShot;
import catchla.yep.model.DribbbleShotImage;

/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotViewHolder extends RecyclerView.ViewHolder implements RequestListener<String, GlideDrawable> {

    private final Fragment fragment;

    private final ImageView imageView;
    private final View imageProgress;

    public DribbbleShotViewHolder(final Fragment fragment, final View itemView) {
        super(itemView);
        this.fragment = fragment;
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
        imageProgress = itemView.findViewById(R.id.image_progress);
    }

    public void displayShot(DribbbleShot shot) {
        final DribbbleShotImage image = getBestImage(shot.getImages());
        if (image != null) {
            imageProgress.setVisibility(View.VISIBLE);
            Glide.with(fragment).load(image.getUrl()).listener(this).into(imageView);
        } else {
            imageProgress.setVisibility(View.GONE);
            Glide.clear(imageView);
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
