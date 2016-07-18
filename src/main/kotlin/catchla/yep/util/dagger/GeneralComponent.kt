package catchla.yep.util.dagger

import android.support.v7.widget.RecyclerView
import catchla.yep.activity.ContentActivity
import catchla.yep.activity.MediaViewerActivity
import catchla.yep.adapter.BaseRecyclerViewAdapter
import catchla.yep.fragment.BaseDialogFragment
import catchla.yep.fragment.BaseFragment
import catchla.yep.loader.CachedYepLoader
import catchla.yep.loader.TileImageLoader
import catchla.yep.menu.HomeMenuActionProvider
import catchla.yep.preference.AccountInfoPreference
import catchla.yep.provider.CacheProvider
import catchla.yep.service.FayeService
import catchla.yep.service.MessageService
import catchla.yep.view.StaticMapView
import dagger.Component
import javax.inject.Singleton

/**
 * Created by mariotaku on 15/10/8.
 */
@Component(modules = arrayOf(ApplicationModule::class))
@Singleton
interface GeneralComponent {
    fun inject(activity: ContentActivity)

    fun inject(fragment: BaseFragment)

    fun inject(fragment: BaseDialogFragment)

    fun inject(service: MessageService)

    fun inject(adapter: HomeMenuActionProvider.HeadersAdapter)

    fun inject(preference: AccountInfoPreference)

    fun inject(adapter: BaseRecyclerViewAdapter<RecyclerView.ViewHolder>)

    fun inject(service: FayeService)

    fun inject(view: StaticMapView)

    fun inject(loader: TileImageLoader)

    fun inject(activity: MediaViewerActivity)

    fun inject(provider: CacheProvider)

    fun inject(loader: CachedYepLoader<Any>)

}
