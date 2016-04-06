package catchla.yep.app;

import android.accounts.Account;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import catchla.yep.Constants;
import catchla.yep.activity.iface.IAccountActivity;
import catchla.yep.service.FayeService;
import catchla.yep.util.DebugModeUtils;
import catchla.yep.util.dagger.ApplicationModule;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mariotaku on 15/5/29.
 */
public class YepApplication extends Application implements Constants {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationModule.get(this);
        Fabric.with(this, new Crashlytics());
        DebugModeUtils.initForApplication(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            ComponentName startedService;
            int foregroundActivitiesCount;

            @Override
            public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(final Activity activity) {
                foregroundActivitiesCount++;
                if (activity instanceof IAccountActivity) {
                    final Intent fayeIntent = new Intent(YepApplication.this, FayeService.class);
                    final Account account = ((IAccountActivity) activity).getAccount();
                    if (account == null) {
                        Log.e(LOGTAG, "Account should not be null here", new Exception());
                    }
                    fayeIntent.putExtra(EXTRA_ACCOUNT, account);
                    startedService = startService(fayeIntent);
                }
            }

            @Override
            public void onActivityResumed(final Activity activity) {

            }

            @Override
            public void onActivityPaused(final Activity activity) {

            }

            @Override
            public void onActivityStopped(final Activity activity) {
                foregroundActivitiesCount--;
                if (foregroundActivitiesCount == 0 && startedService != null) {
                    stopService(new Intent().setComponent(startedService));
                }
            }

            @Override
            public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(final Activity activity) {

            }
        });
    }

}
