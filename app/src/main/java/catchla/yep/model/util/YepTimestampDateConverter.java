package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.util.Date;

/**
 * Created by mariotaku on 15/6/28.
 */
public class YepTimestampDateConverter extends StringBasedTypeConverter<Date> {
    @Override
    public Date getFromString(final String s) {
        return new Date((long) (Double.parseDouble(s) * 1000));
    }

    @Override
    public String convertToString(final Date date) {
        return String.valueOf(date.getTime() / 1000.0);
    }
}
