package catchla.yep.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.dagger.GeneralComponentHelper;

/**
 * Created by mariotaku on 15/10/13.
 */
public abstract class BaseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final Context context;
    @Inject
    protected ImageLoaderWrapper mImageLoader;

    public BaseRecyclerViewAdapter(Context context) {
        //noinspection unchecked
        GeneralComponentHelper.build(context).inject((BaseRecyclerViewAdapter<RecyclerView.ViewHolder>) this);
        this.context = context;
    }

    public ImageLoaderWrapper getImageLoader() {
        return mImageLoader;
    }

    public Context getContext() {
        return context;
    }
}
