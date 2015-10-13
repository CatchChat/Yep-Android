package catchla.yep.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import javax.inject.Inject;

import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.dagger.ApplicationModule;
import catchla.yep.util.dagger.DaggerGeneralComponent;

/**
 * Created by mariotaku on 15/10/13.
 */
public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    @Inject
    protected ImageLoaderWrapper mImageLoader;

    public BaseRecyclerViewAdapter(Context context) {
        DaggerGeneralComponent.builder().applicationModule(ApplicationModule.get(context)).build().inject(this);
        this.context = context;
    }

    public ImageLoaderWrapper getImageLoader() {
        return mImageLoader;
    }

    public Context getContext() {
        return context;
    }
}
