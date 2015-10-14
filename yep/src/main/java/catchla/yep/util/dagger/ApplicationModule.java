package catchla.yep.util.dagger;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.IOException;

import catchla.yep.BuildConfig;
import catchla.yep.app.YepApplication;
import catchla.yep.util.ImageLoaderWrapper;
import dagger.Module;
import dagger.Provides;

/**
 * Created by mariotaku on 15/10/8.
 */
@Module
public class ApplicationModule {

    private final Bus bus;
    private final ImageLoader imageLoader;
    private final YepApplication application;
    private final ImageLoaderWrapper imageLoaderWrapper;

    public ApplicationModule(YepApplication application) {
        this.application = application;
        bus = new Bus(ThreadEnforcer.MAIN);
        imageLoader = createImageLoader();
        imageLoaderWrapper = new ImageLoaderWrapper(imageLoader);
    }

    @Provides
    public ImageLoaderWrapper getImageLoaderWrapper() {
        return imageLoaderWrapper;
    }

    public static ApplicationModule get(final Context context) {
        return ((YepApplication) context.getApplicationContext()).getApplicationModule();
    }

    private ImageLoader createImageLoader() {
        final ImageLoader loader = ImageLoader.getInstance();
        final ImageLoaderConfiguration.Builder cb = new ImageLoaderConfiguration.Builder(application);
        cb.threadPriority(Thread.NORM_PRIORITY - 2);
        try {
            cb.diskCache(new LruDiskCache(application.getCacheDir(), new Md5FileNameGenerator(), 256 * 1024 * 1024));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cb.denyCacheImageMultipleSizesInMemory();
        cb.tasksProcessingOrder(QueueProcessingType.LIFO);
        L.writeDebugLogs(BuildConfig.DEBUG);
        loader.init(cb.build());
        return loader;
    }

    @Provides
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Provides
    public Bus getBus() {
        return bus;
    }
}
