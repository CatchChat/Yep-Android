package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by mariotaku on 15/5/29.
 */
public class ISO8601DateConverter extends DateTypeConverter {
    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    }

}
