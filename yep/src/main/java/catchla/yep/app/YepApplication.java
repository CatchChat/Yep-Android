package catchla.yep.app;

import android.app.Application;

import com.squareup.picasso.Picasso;

import catchla.yep.BuildConfig;
import catchla.yep.util.DebugModeUtils;

/**
 * Created by mariotaku on 15/5/29.
 */
public class YepApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugModeUtils.initForApplication(this);
        Picasso.with(this).setLoggingEnabled(BuildConfig.DEBUG);
    }

}
