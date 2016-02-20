package catchla.yep.model.util;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by mariotaku on 16/1/6.
 */
public class SerializeOnlyJsonArrayConverter implements TypeConverter<List<Object>> {
    @Override
    public List<Object> parse(final JsonParser jsonParser) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(final List<Object> list, final String fieldName, final boolean writeFieldNameForObject, final JsonGenerator jsonGenerator) throws IOException {
        if (writeFieldNameForObject) {
            jsonGenerator.writeFieldName(fieldName);
        }
        if (list != null) {
            jsonGenerator.writeStartArray();
            for (final Object item : list) {
                final JsonMapper mapper = LoganSquare.mapperFor(item.getClass());
                //noinspection unchecked
                mapper.serialize(item, jsonGenerator, true);
            }
            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeNull();
        }
    }
}
