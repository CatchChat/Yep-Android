package android.support.v4.content;

/**
 * Created by mariotaku on 15/7/5.
 */
public class LoaderTrojan {
    public static <T> boolean isContentChanged(final Loader<T> loader) {
        return loader.mContentChanged;
    }
}
