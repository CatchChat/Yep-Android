package catchla.yep.util.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.IOException;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.app.YepApplication;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.YepImageDownloader;
import dagger.Module;
import dagger.Provides;

/**
 * Created by mariotaku on 15/10/8.
 */
@Module
public class ApplicationModule implements Constants {

    private final Bus bus;
    private final ImageLoader imageLoader;
    private final YepApplication application;
    private final ImageLoaderWrapper imageLoaderWrapper;
    private final YepImageDownloader imageDownloader;
    private final DiskCache diskCache;
    private final SharedPreferences sharedPreferences;

    public ApplicationModule(YepApplication application) {
        this.application = application;
        bus = new Bus(ThreadEnforcer.MAIN);
        imageDownloader = createImageDownloader(application);
        diskCache = createDiskCache();
        imageLoader = createImageLoader(imageDownloader, diskCache);
        imageLoaderWrapper = new ImageLoaderWrapper(imageLoader);
        sharedPreferences = application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static ApplicationModule get(final Context context) {
        return ((YepApplication) context.getApplicationContext()).getApplicationModule();
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

    public YepImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    private YepImageDownloader createImageDownloader(final YepApplication application) {
        return new YepImageDownloader(application);
    }

    @Provides
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    @Provides
    public ImageLoaderWrapper getImageLoaderWrapper() {
        return imageLoaderWrapper;
    }

    private DiskCache createDiskCache() {
        try {
            return new LruDiskCache(application.getCacheDir(), new Md5FileNameGenerator(), 256 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ImageLoader createImageLoader(ImageDownloader downloader, DiskCache cache) {
        final ImageLoader loader = ImageLoader.getInstance();
        final ImageLoaderConfiguration.Builder cb = new ImageLoaderConfiguration.Builder(application);
        cb.threadPriority(Thread.NORM_PRIORITY - 2);
        cb.diskCache(cache);
        final DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.resetViewBeforeLoading(true);
        cb.defaultDisplayImageOptions(builder.build());
        cb.imageDownloader(downloader);
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
