package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mariotaku on 15/5/29.
 */
public class AWSDateConverter extends DateTypeConverter {
    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    }

    @Override
    public Date parse(final JsonParser jsonParser) throws IOException {
        return super.parse(jsonParser);
    }
}
