package catchla.yep.model.util;

import android.content.ContentValues;
import android.database.Cursor;

import org.mariotaku.library.objectcursor.converter.CursorFieldConverter;

import java.lang.reflect.ParameterizedType;

/**
 * Created by mariotaku on 15/12/3.
 */
public class NaNIfNullDoubleConverter implements CursorFieldConverter<Double> {
    @Override
    public Double parseField(final Cursor cursor, final int columnIndex, final ParameterizedType fieldType) {
        return cursor.isNull(columnIndex) ? Double.NaN : cursor.getDouble(columnIndex);
    }

    @Override
    public void writeField(final ContentValues values, final Double object, final String columnName, final ParameterizedType fieldType) {
        values.put(columnName, object);
    }
}
