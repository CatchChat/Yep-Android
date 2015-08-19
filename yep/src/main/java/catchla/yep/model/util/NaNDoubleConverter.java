package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

/**
 * Created by mariotaku on 15/8/19.
 */
public class NaNDoubleConverter extends StringBasedTypeConverter<Double> {
    @Override
    public Double getFromString(final String str) {
        if (str == null) return Double.NaN;
        return Double.parseDouble(str);
    }

    @Override
    public String convertToString(final Double obj) {
        if (obj == null) return null;
        return String.valueOf(obj);
    }
}
