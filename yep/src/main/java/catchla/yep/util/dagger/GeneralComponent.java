package catchla.yep.util.dagger;

import catchla.yep.activity.ContentActivity;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.fragment.BaseFragment;
import catchla.yep.menu.HomeMenuActionProvider;
import catchla.yep.preference.AccountInfoPreference;
import catchla.yep.service.MessageService;
import dagger.Component;

/**
 * Created by mariotaku on 15/10/8.
 */
@Component(modules = ApplicationModule.class)
public interface GeneralComponent {
    void inject(final ContentActivity activity);

    void inject(final BaseFragment fragment);

    void inject(MessageService service);


    void inject(HomeMenuActionProvider.HeadersAdapter adapter);

    void inject(AccountInfoPreference preference);


    void inject(BaseRecyclerViewAdapter adapter);
}
