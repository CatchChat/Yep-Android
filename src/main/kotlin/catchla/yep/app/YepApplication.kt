package catchla.yep.app

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import catchla.yep.Constants
import catchla.yep.activity.iface.IAccountActivity
import catchla.yep.service.FayeService
import catchla.yep.util.DebugModeUtils
import catchla.yep.util.dagger.ApplicationModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by mariotaku on 15/5/29.
 */
class YepApplication : Application(), Constants {

    override fun onCreate() {
        super.onCreate()
        ApplicationModule.get(this)
        Fabric.with(this, Crashlytics())
        DebugModeUtils.initForApplication(this)

        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            internal var startedService: ComponentName? = null
            internal var foregroundActivitiesCount: Int = 0

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
                foregroundActivitiesCount++
                if (activity is IAccountActivity) {
                    val fayeIntent = Intent(this@YepApplication, FayeService::class.java)
                    val account = activity.account
                    fayeIntent.putExtra(Constants.EXTRA_ACCOUNT, account)
                    startedService = startService(fayeIntent)
                }
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                foregroundActivitiesCount--
                if (foregroundActivitiesCount == 0 && startedService != null) {
                    stopService(Intent().setComponent(startedService))
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

}
