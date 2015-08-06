package catchla.yep.util;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

/**
 * Created by mariotaku on 15/8/6.
 */
public class JsonSerializer {
    public static <T> String serialize(final List<T> list, final Class<T> cls) {
        try {
            return LoganSquare.serialize(list, cls);
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
}
