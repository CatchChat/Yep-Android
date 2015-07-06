package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import catchla.yep.R;
import catchla.yep.model.InstagramImage;
import catchla.yep.model.InstagramMedia;

/**
 * Created by mariotaku on 15/6/3.
 */
public class InstagramMediaViewHolder extends RecyclerView.ViewHolder implements Callback {
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
            Picasso.with(itemView.getContext()).load(image.getUrl()).into(imageView, this);
        } else {
            imageProgress.setVisibility(View.GONE);
            Picasso.with(itemView.getContext()).cancelRequest(imageView);
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
    public void onSuccess() {
        imageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        imageProgress.setVisibility(View.GONE);
    }
}
