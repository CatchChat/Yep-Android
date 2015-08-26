package catchla.yep.model.util;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.NoSuchMapperException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import org.mariotaku.restfu.http.ValueMap;

import java.io.IOException;

/**
 * Created by mariotaku on 15/8/26.
 */
public class ValueMapJsonMapper<T extends ValueMap> extends JsonMapper<T> {

    @Override
    public T parse(final JsonParser jsonParser) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serialize(final T obj, final JsonGenerator jsonGenerator, final boolean writeStartEndObject) throws IOException {
        if (writeStartEndObject) {
            jsonGenerator.writeStartObject();
        }
        for (String key : obj.keys()) {
            jsonGenerator.writeFieldName(key);
            writeObject(jsonGenerator, obj.get(key));
        }
        if (writeStartEndObject) {
            jsonGenerator.writeEndObject();
        }
    }

    private void writeObject(final JsonGenerator jsonGenerator, final Object obj) throws IOException {
        if (obj instanceof String) {
            jsonGenerator.writeString((String) obj);
        } else if (obj instanceof Boolean) {
            jsonGenerator.writeBoolean((Boolean) obj);
        } else if (obj instanceof Number) {
            jsonGenerator.writeNumber(obj.toString());
        } else {
            //noinspection unchecked
            final Class<Object> cls = (Class<Object>) obj.getClass();
            try {
                final JsonMapper<Object> jsonMapper = LoganSquare.mapperFor(cls);
                jsonMapper.serialize(obj, jsonGenerator, true);
            } catch (NoSuchMapperException e) {
                // Ignore first
            }
        }
    }
}
