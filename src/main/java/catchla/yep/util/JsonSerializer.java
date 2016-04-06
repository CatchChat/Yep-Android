package catchla.yep.util;

import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariotaku on 15/8/6.
 */
public class JsonSerializer {
    @Nullable
    public static <T> String serialize(@Nullable final List<T> list, final Class<T> cls) {
        if (list == null) return null;
        try {
            return LoganSquare.serialize(list, cls);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> String serialize(@Nullable final T object, final Class<T> cls) {
        if (object == null) return null;
        try {
            return LoganSquare.mapperFor(cls).serialize(object);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> String serializeArray(@Nullable final T[] object, final Class<T> cls) {
        if (object == null) return null;
        try {
            return LoganSquare.mapperFor(cls).serialize(Arrays.asList(object));
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> List<T> parseList(@Nullable final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            return LoganSquare.mapperFor(cls).parseList(string);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T[] parseArray(@Nullable final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            final List<T> list = LoganSquare.mapperFor(cls).parseList(string);
            //noinspection unchecked
            return list.toArray((T[]) Array.newInstance(cls, list.size()));
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T parse(@Nullable final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            return LoganSquare.mapperFor(cls).parse(string);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T parse(@Nullable final InputStream stream, final Class<T> cls) {
        if (stream == null) return null;
        try {
            return LoganSquare.mapperFor(cls).parse(stream);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static <T> String serialize(@Nullable final T obj) {
        if (obj == null) return null;
        //noinspection unchecked
        return serialize(obj, (Class<T>) obj.getClass());
    }
}
