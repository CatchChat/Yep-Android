package catchla.yep.app;

import android.app.Application;

import catchla.yep.util.DebugModeUtils;
import catchla.yep.util.dagger.ApplicationModule;

/**
 * Created by mariotaku on 15/5/29.
 */
public class YepApplication extends Application {

    private ApplicationModule mApplicationModule;

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationModule();
        DebugModeUtils.initForApplication(this);
    }

    public ApplicationModule getApplicationModule() {
        if (mApplicationModule != null) return mApplicationModule;
        return mApplicationModule = new ApplicationModule(this);
    }
}
