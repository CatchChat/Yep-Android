package catchla.yep.util.dagger;

import android.support.v7.widget.RecyclerView;

import javax.inject.Singleton;

import catchla.yep.activity.ContentActivity;
import catchla.yep.activity.MediaViewerActivity;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.fragment.BaseFragment;
import catchla.yep.fragment.ChatMediaBottomSheetDialogFragment;
import catchla.yep.loader.CachedYepLoader;
import catchla.yep.loader.TileImageLoader;
import catchla.yep.menu.HomeMenuActionProvider;
import catchla.yep.preference.AccountInfoPreference;
import catchla.yep.provider.CacheProvider;
import catchla.yep.service.FayeService;
import catchla.yep.service.MessageService;
import catchla.yep.view.StaticMapView;
import dagger.Component;

/**
 * Created by mariotaku on 15/10/8.
 */
@Component(modules = ApplicationModule.class)
@Singleton
public interface GeneralComponent {
    void inject(final ContentActivity activity);

    void inject(final BaseFragment fragment);

    void inject(MessageService service);


    void inject(HomeMenuActionProvider.HeadersAdapter adapter);

    void inject(AccountInfoPreference preference);

    void inject(BaseRecyclerViewAdapter<RecyclerView.ViewHolder> adapter);

    void inject(FayeService service);

    void inject(StaticMapView view);

    void inject(TileImageLoader loader);

    void inject(MediaViewerActivity activity);

    void inject(CacheProvider provider);

    void inject(CachedYepLoader<Object> loader);

}
