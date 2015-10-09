package catchla.yep.util.dagger;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import catchla.yep.activity.ContentActivity;
import catchla.yep.app.YepApplication;
import dagger.Module;
import dagger.Provides;

/**
 * Created by mariotaku on 15/10/8.
 */
@Module
public class ApplicationModule {
    private final Bus bus;

    public ApplicationModule(YepApplication application) {
        bus = new Bus(ThreadEnforcer.MAIN);
    }

    @Provides
    public Bus getBus() {
        return bus;
    }

    public static ApplicationModule get(final Context context) {
        return ((YepApplication)context.getApplicationContext()).getApplicationModule();
    }
}
