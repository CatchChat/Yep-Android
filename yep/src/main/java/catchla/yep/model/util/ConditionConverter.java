package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

import catchla.yep.model.S3UploadToken;

/**
 * Created by mariotaku on 15/6/2.
 */
public class ConditionConverter implements TypeConverter<S3UploadToken.Condition> {
    @Override
    public S3UploadToken.Condition parse(final JsonParser jsonParser) throws IOException {
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return null;
        }
        if (jsonParser.nextToken() != JsonToken.FIELD_NAME) {
            jsonParser.skipChildren();
            return null;
        }
        final String name = jsonParser.getCurrentName();
        final S3UploadToken.Condition condition = new S3UploadToken.Condition();
        condition.setName(name);
        jsonParser.nextToken();
        condition.setValue(jsonParser.getValueAsString());
        jsonParser.skipChildren();
        jsonParser.nextToken();
        return condition;
    }

    @Override
    public void serialize(final S3UploadToken.Condition object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName(object.getName());
            jsonGenerator.writeString(object.getValue());
            jsonGenerator.writeEndObject();
        }
    }
}
