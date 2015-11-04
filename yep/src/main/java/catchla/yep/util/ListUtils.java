package catchla.yep.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created by mariotaku on 15/11/4.
 */
public class ListUtils {
    @NonNull
    public static <T> List<T> nonNullList(@Nullable final List<T> list) {
        if (list != null) return list;
        return Collections.emptyList();
    }
}
