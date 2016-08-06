package catchla.yep.util.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

import org.mariotaku.mediaviewer.library.FileCache;
import org.mariotaku.mediaviewer.library.MediaDownloader;

import java.io.IOException;

import javax.inject.Singleton;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.OkMediaDownloader;
import catchla.yep.util.UILFileCache;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepImageDownloader;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by mariotaku on 15/10/8.
 */
@Module
public class ApplicationModule implements Constants {

    private static ApplicationModule sInstance;

    private final Application application;

    ApplicationModule(Application application) {
        this.application = application;
    }

    public static ApplicationModule get(final Context context) {
        if (sInstance != null) return sInstance;
        return sInstance = new ApplicationModule((Application) context.getApplicationContext());
    }

    @Provides
    @Singleton
    public DiskCache getDiskCache() {
        return createDiskCache();
    }

    @Provides
    @Singleton
    public ImageDownloader imageDownloader(final MediaDownloader downloader) {
        return new YepImageDownloader(application, downloader);
    }

    @Provides
    @Singleton
    public FileCache fileCache(DiskCache cache) {
        return new UILFileCache(cache);
    }

    @Provides
    @Singleton
    public MediaDownloader mediaDownloader(OkHttpClient client) {
        return new OkMediaDownloader(client);
    }

    @Provides
    @Singleton
    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    public ImageLoaderWrapper getImageLoaderWrapper(final ImageLoader imageLoader) {
        final DisplayImageOptions defaultDisplayImageOptions = createDefaultDisplayImageOptions();
        return new ImageLoaderWrapper(application, imageLoader, defaultDisplayImageOptions);
    }

    private DiskCache createDiskCache() {
        try {
            return new LruDiskCache(application.getCacheDir(), new Md5FileNameGenerator(), 256 * 1024 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ImageLoader createImageLoader(ImageDownloader downloader, DiskCache cache, DisplayImageOptions defaultDisplayImageOptions) {
        final ImageLoader loader = ImageLoader.getInstance();
        final ImageLoaderConfiguration.Builder cb = new ImageLoaderConfiguration.Builder(application);
        cb.threadPriority(Thread.NORM_PRIORITY - 2);
        cb.diskCache(cache);
        cb.defaultDisplayImageOptions(defaultDisplayImageOptions);
        cb.imageDownloader(downloader);
        cb.denyCacheImageMultipleSizesInMemory();
        cb.tasksProcessingOrder(QueueProcessingType.LIFO);
        L.writeDebugLogs(BuildConfig.DEBUG);
        loader.init(cb.build());
        return loader;
    }

    private DisplayImageOptions createDefaultDisplayImageOptions() {
        final DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.resetViewBeforeLoading(true);
        return builder.build();
    }

    @Provides
    @Singleton
    public ImageLoader getImageLoader(final ImageDownloader imageDownloader, final DiskCache diskCache) {
        return createImageLoader(imageDownloader, diskCache, createDefaultDisplayImageOptions());
    }

    @Provides
    @Singleton
    public Bus getBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides
    @Singleton
    public OkHttpClient okHttpClient() {
        return YepAPIFactory.getOkHttpClient(application);
    }
}
