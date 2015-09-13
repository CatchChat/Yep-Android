package catchla.yep.util;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mariotaku on 15/8/6.
 */
public class JsonSerializer {
    public static <T> String serialize(final List<T> list, final Class<T> cls) {
        if (list == null) return null;
        try {
            return LoganSquare.serialize(list, cls);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> String serialize(final T object, final Class<T> cls) {
        if (object == null) return null;
        try {
            return LoganSquare.mapperFor(cls).serialize(object);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> String serializeArray(final T[] object, final Class<T> cls) {
        if (object == null) return null;
        try {
            return LoganSquare.mapperFor(cls).serialize(Arrays.asList(object));
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> List<T> parseList(final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            return LoganSquare.mapperFor(cls).parseList(string);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T[] parseArray(final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            final List<T> list = LoganSquare.mapperFor(cls).parseList(string);
            //noinspection unchecked
            return list.toArray((T[]) Array.newInstance(cls, list.size()));
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T parse(final String string, final Class<T> cls) {
        if (string == null) return null;
        try {
            return LoganSquare.mapperFor(cls).parse(string);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> String serialize(final T obj) {
        if (obj == null) return null;
        //noinspection unchecked
        return serialize(obj, (Class<T>) obj.getClass());
    }
}
